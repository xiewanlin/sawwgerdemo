package com.xwl.learn.aop.idempotent;

import com.xwl.learn.aop.idempotent.Idempotent.LockFailAction;
import com.xwl.learn.utils.SpringContextHolder;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * @author xiewanlin
 * @version 1.0
 * @className IdempotentAspect
 * @description 幂等切面
 * @date 2019/7/30 13:36
 **/
@Aspect
@Component
@Slf4j
@Order(1)
public class IdempotentAspect {

	@Autowired
	private RedissonClient redissonClient;

	private final String DEFAULT_CACHE_PREFIX ="idempotent";

	@Pointcut("@annotation(com.xwl.learn.aop.idempotent.Idempotent)")
	private void pointcut(){

	}

	/**
	 * 1.根据注解的Spel表达式提取key值
	 * 2.根据key值提取缓存中数据
	 * 3.1存在，直接返回结果
	 * 4.1不存在，加分布式锁
	 * 4.2获取锁成功后再判断缓存是否存在值，避免虚假唤醒
	 * 4.3执行目标方法
	 * 4.4缓存结果对象
	 * 4.5进行锁释放
	 * @param pjp
	 * @param idempotent
	 * @return
	 * @throws Throwable
	 */
	@Around("pointcut()&&@annotation(idempotent)")
	public Object idempotentHandle(ProceedingJoinPoint pjp , Idempotent idempotent) throws Throwable{

		Object[] args = pjp.getArgs();
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		//step 1
		Object value = this.parseSpel(idempotent.key(), method, args);
		String cacheKey = String.format("%s:%s:%s",DEFAULT_CACHE_PREFIX,idempotent.namespace(),value);
		log.info("解析幂等key成功,key:{}",cacheKey);
		//step 2
		RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
		Object result = this.getResult(cacheKey, args, idempotent);
		if(Objects.nonNull(result)){
			//step 3.1
			log.info("幂等命中直接返回,key:{}",cacheKey);
			return result;
		}else {
			//step 4.1
			String lockKey = String.format("%s:%s:%s:%s",DEFAULT_CACHE_PREFIX,idempotent.namespace(),value,"lock");
			RLock lock = redissonClient.getLock(lockKey);
			if(this.lock(lock, idempotent)){
				try {
					log.info("幂等加锁成功,key:{}",cacheKey);
					//step 4.2
					result = this.getResult(cacheKey, args, idempotent);
					if(Objects.nonNull(result)){
						log.info("幂等命中直接返回,key:{}",cacheKey);
						return result;
					}
					//step 4.3
					log.info("幂等未命中,开始调用目标方法,key:{}",cacheKey);
					Object resultObj = pjp.proceed();
					//step 4.4
					this.cacheResult(bucket,resultObj,idempotent);
					return  resultObj;
				}finally{
					//step 4.5
					if(lock.isHeldByCurrentThread()){
						lock.unlock();
					}
				}
			}else {
				log.info("幂等加锁失败,key：{}",cacheKey);
				throw new Exception();
			}
		}

	}

	private void cacheResult(RBucket<Object> bucket,Object resultObj,Idempotent idempotent){
		try {
			bucket.set(resultObj,idempotent.cacheTime(),TimeUnit.SECONDS);
		} catch (Exception ex){
			log.error("幂等结果缓存失败,result:{}",resultObj,ex);
		}
	}

	/**
	 * 提取幂等的结果数据
	 * @param cacheKey
	 * @param args
	 * @param idempotent
	 * @return
	 */
	private Object getResult(String cacheKey,Object[] args,Idempotent idempotent){
		RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
		if(bucket.isExists()){
			return bucket.get();
		}else {
			Class<? extends BaseIdempotent> resultClass = idempotent.result();
			if(DefaultBaseIdempotent.class != resultClass){
				BaseIdempotent baseIdempotent = SpringContextHolder.getBean(resultClass);
				log.info("开始调用服务类{}，提取幂等结果",resultClass.getSimpleName());
				long startTime = System.currentTimeMillis();
				Object data = baseIdempotent.data(args);
				long endTime = System.currentTimeMillis();
				log.info("调用服务类{}，提取幂等结果,总耗时:{}ms",resultClass.getSimpleName(),endTime-startTime);
				this.cacheResult(bucket,data,idempotent);
				return data;
			}
		}
		return null;
	}

	/**
	 * 根据锁策略进行加锁
	 * @param lock
	 * @param idempotent
	 * @return
	 * @throws Throwable
	 */
	private boolean lock(RLock lock,Idempotent idempotent) throws Throwable{
		if(idempotent.action() == LockFailAction.CONTINUE) {
			int i = 1;
			while (!lock.tryLock(idempotent.lockWaitTime(), TimeUnit.MILLISECONDS)) {
					log.info("开始循环获取锁，name:{},当前循环次数{}",lock.getName(),i++);
			}
			return true;
		}else{
			return lock.tryLock(idempotent.lockWaitTime(), TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * 解析SpEL表达式
	 *
	 * @param key SpEL表达式
	 * @param method 反射得到的方法
	 * @param args 反射得到的方法参数
	 * @return 解析后SpEL表达式对应的值
	 */
	private Object parseSpel(String key, Method method, Object[] args) {
		// 创建解析器
		ExpressionParser parser = new SpelExpressionParser();
		// 通过Spring的LocalVariableTableParameterNameDiscoverer获取方法参数名列表
		LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		// 构造上下文
		EvaluationContext context = new StandardEvaluationContext();
		if (args.length == parameterNames.length) {
			for (int i = 0, len = args.length; i < len; i++) {
				// 使用setVariable方法来注册自定义变量
				context.setVariable(parameterNames[i], args[i]);
			}
		}
		if (args.length == 0 && parameterNames.length == 0) {
			return key;
		}
		return parser.parseExpression(key).getValue(context);
	}
}
