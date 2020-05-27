package com.xwl.learn.config;

import static com.xwl.learn.config.MQConfig.CompensationMQDef.FAILED_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.FIVE_MIN_DELAY_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.ONE_MIN_DELAY_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.TEN_MIN_DELAY_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.WORK_MQ_DEF;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 消息定义
 * @date 2019/12/30 19:19
 **/
@Configuration
public class MQConfig {

	public final static long MILLISECOND_PER_HOUR = (long) 1000 * 60 * 60;

	public static final  String  DEF_PREFIX = "payment-server.compensation.";

	public static final  String  WORK_QUEUE_NAME = DEF_PREFIX+"workQueue";
	public static final  String  ONE_MIN_DELAY_QUEUE_NAME = DEF_PREFIX+"delayQueue@1";
	public static final  String  FIVE_MIN_DELAY_QUEUE_NAME = DEF_PREFIX+"delayQueue@5";
	public static final  String  TEN_MIN_DELAY_QUEUE_NAME = DEF_PREFIX+"delayQueue@10";
	public static final  String  FAILED_QUEUE_NAME = DEF_PREFIX+"failedQueue";

	public enum CompensationMQDef{
		WORK_MQ_DEF(DEF_PREFIX+"workExchange",DEF_PREFIX+"workRoute",WORK_QUEUE_NAME,0),
		ONE_MIN_DELAY_MQ_DEF(DEF_PREFIX+"oneMinExchange",DEF_PREFIX+"oneMinRoute",ONE_MIN_DELAY_QUEUE_NAME,60),
		FIVE_MIN_DELAY_MQ_DEF(DEF_PREFIX+"fiveMinExchange",DEF_PREFIX+"fiveMinRoute",FIVE_MIN_DELAY_QUEUE_NAME,60*5),
		TEN_MIN_DELAY_MQ_DEF(DEF_PREFIX+"tenMinExchange",DEF_PREFIX+"tenMinRoute",TEN_MIN_DELAY_QUEUE_NAME,60*10),
		FAILED_MQ_DEF(DEF_PREFIX+"failedQueue",DEF_PREFIX+"failedRoute",FAILED_QUEUE_NAME,0);
		private  String exchangeName;
		private  String routeName;
		private  String queueName;
		private  Integer delaySecs;

		CompensationMQDef(String exchangeName,String routeName,
				String queueName,Integer delaySecs){
			this.exchangeName = exchangeName;
			this.routeName = routeName;
			this.queueName = queueName;
			this.delaySecs = delaySecs;
		}

		public String getExchangeName() {
			return exchangeName;
		}

		public String getRouteName() {
			return routeName;
		}

		public String getQueueName() {
			return queueName;
		}

		public Integer getDelaySecs() {
			return delaySecs;
		}

		public void setDelaySecs(Integer delaySecs) {
			this.delaySecs = delaySecs;
		}
	}

	@Bean("compensationWorkExchange")
	public DirectExchange workExchange(){
		return new DirectExchange(WORK_MQ_DEF.exchangeName,true,false);
	}

	@Bean("compensationWorkQueue")
	public Queue workQueue(){
		Map<String, Object> args = new HashMap<>(4);
		args.put("x-dead-letter-exchange", FAILED_MQ_DEF.exchangeName);
		args.put("x-dead-letter-routing-key", FAILED_MQ_DEF.routeName);
		//工作队列超过一个小时未消费，自动进入失败队列
		args.put("x-message-ttl", MILLISECOND_PER_HOUR);
		return new Queue(WORK_MQ_DEF.queueName,true);
	}

	@Bean("compensationWorkBind")
	public Binding workBind(){
		return BindingBuilder.bind(workQueue()).to(workExchange()).with(WORK_MQ_DEF.routeName);
	}

	@Bean("compensationOneMinDelayExchange")
	public DirectExchange oneMinDelayExchange(){
		return new DirectExchange(ONE_MIN_DELAY_MQ_DEF.exchangeName,true,false);
	}

	@Bean("compensationOneMinDelayQueue")
	public Queue oneMinDelayQueue(){
		Map<String, Object> args = new HashMap<>(3);
		args.put("x-dead-letter-exchange", WORK_MQ_DEF.exchangeName);
		args.put("x-dead-letter-routing-key", WORK_MQ_DEF.routeName);
		//延迟队列最长延迟时间一个小时
		args.put("x-message-ttl", MILLISECOND_PER_HOUR);
		return new Queue(ONE_MIN_DELAY_MQ_DEF.queueName,true, false, false, args);
	}

	@Bean("compensationOneMinDelayBind")
	public Binding oneMinDelayBind(){
		return BindingBuilder.bind(oneMinDelayQueue()).to(oneMinDelayExchange()).with(ONE_MIN_DELAY_MQ_DEF.routeName);
	}

	@Bean("compensationFiveMinDelayExchange")
	public DirectExchange fiveMinDelayExchange(){
		return new DirectExchange(FIVE_MIN_DELAY_MQ_DEF.exchangeName,true,false);
	}

	@Bean("compensationFiveMinDelayQueue")
	public Queue fiveMinDelayQueue(){
		Map<String, Object> args = new HashMap<>(3);
		args.put("x-dead-letter-exchange", WORK_MQ_DEF.exchangeName);
		args.put("x-dead-letter-routing-key", WORK_MQ_DEF.routeName);
		//延迟队列最长延迟时间一个小时
		args.put("x-message-ttl", MILLISECOND_PER_HOUR);
		return new Queue(FIVE_MIN_DELAY_MQ_DEF.queueName,true, false, false, args);
	}

	@Bean("compensationFiveMinDelayBind")
	public Binding fiveMinDelayBind(){
		return BindingBuilder.bind(fiveMinDelayQueue()).to(fiveMinDelayExchange()).with(FIVE_MIN_DELAY_MQ_DEF.routeName);
	}

	@Bean("compensationTenMinDelayExchange")
	public DirectExchange tenMinDelayExchange(){
		return new DirectExchange(TEN_MIN_DELAY_MQ_DEF.exchangeName,true,false);
	}

	@Bean("compensationTenMinDelayQueue")
	public Queue tenMinDelayQueue(){
		Map<String, Object> args = new HashMap<>(3);
		args.put("x-dead-letter-exchange", WORK_MQ_DEF.exchangeName);
		args.put("x-dead-letter-routing-key", WORK_MQ_DEF.routeName);
		//延迟队列最长延迟时间一个小时
		args.put("x-message-ttl", MILLISECOND_PER_HOUR);
		return new Queue(TEN_MIN_DELAY_MQ_DEF.queueName,true, false, false, args);
	}

	@Bean("compensationTenMinDelayBind")
	public Binding tenMinDelayBind(){
		return BindingBuilder.bind(tenMinDelayQueue()).to(tenMinDelayExchange()).with(TEN_MIN_DELAY_MQ_DEF.routeName);
	}

	@Bean("compensationFailExchange")
	public DirectExchange compensationFailExchange(){
		return new DirectExchange(FAILED_MQ_DEF.exchangeName,true,false);
	}

	@Bean("compensationFailQueue")
	public Queue compensationFailQueue(){
		return new Queue(FAILED_MQ_DEF.queueName,true);
	}

	@Bean("compensationFailBind")
	public Binding compensationFailBind(){
		return BindingBuilder.bind(compensationFailQueue()).to(compensationFailExchange()).with(FAILED_MQ_DEF.routeName);
	}


}
