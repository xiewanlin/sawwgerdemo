package com.xwl.learn.aop.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 幂等注解
 * @date 2019/7/30 13:36
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {


	String namespace() default "payment";

	/**
	 * 通过Spel语法提取对应值，作为缓存中key
	 * 实例 #变量名1.属性名1+#变量名2.属性名2
	 * @return
	 */
	String key() default "";


	/**
	 * 幂等有效时间，如果需要长期有效，必须设置result类型，实现BaseIdempotent类
	 * 即接口结果在缓存保存的时间
	 * 单位：秒
	 * @return
	 */
	int cacheTime()  default 600;

	/**
	 * 用于从数据库拉取幂等数据
	 * 该类需要使用@Component注入到spring容器中
	 * @return
	 */
	Class<? extends BaseIdempotent> result() default DefaultBaseIdempotent.class;

	/**
	 * 分布式锁加锁等待时间，单位：毫秒
	 * @return
	 */
	int lockWaitTime() default 1000;

	/**
	 * 加锁失败处理方式
	 * @return
	 */
	LockFailAction action() default LockFailAction.CONTINUE;

	enum LockFailAction {GIVE_UP, CONTINUE}

}
