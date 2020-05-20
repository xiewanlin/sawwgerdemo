package com.xwl.learn.config;


import static com.xwl.learn.config.MQCommonConfig.MQDef.CALLBACK_MQ_DEF;
import static com.xwl.learn.config.MQCommonConfig.MQDef.PAYMENT_ORDER_CALLBACK_MQ_DEF;
import static com.xwl.learn.config.MQCommonConfig.MQDef.PAYMENT_STATUS_DELAY_MQ_DEF;
import static com.xwl.learn.config.MQCommonConfig.MQDef.PAYMENT_STATUS_WORK_MQ_DEF;

import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQCommonConfig {

	public static final  String  DEF_PREFIX = "payment-server.";

	public static final  String  CALLBACK_QUEUE_NAME = DEF_PREFIX+"payment.callbackQueue";
	public static final  String  PAYMENT_STATUS_DELAY_QUEUE_NAME = DEF_PREFIX+"paymentStatus.delay.queue";
	public static final  String  PAYMENT_STATUS_WORK_QUEUE_NAME = DEF_PREFIX+"paymentStatus.work.queue";
	public static final  String  PAYMENT_ORDER_CALLBACK_QUEUE_NAME = DEF_PREFIX+"payment.order.callbackQueue";


	public enum MQDef{
		CALLBACK_MQ_DEF(DEF_PREFIX+"payment.callbackExchange",DEF_PREFIX+"payment.callbackRoute",CALLBACK_QUEUE_NAME),
		PAYMENT_STATUS_DELAY_MQ_DEF(DEF_PREFIX+"paymentStatus.delay.exchange",DEF_PREFIX+"paymentStatus.delay.route",PAYMENT_STATUS_DELAY_QUEUE_NAME),
		PAYMENT_STATUS_WORK_MQ_DEF(DEF_PREFIX+"paymentStatus.work.exchange",DEF_PREFIX+"paymentStatus.work.route",PAYMENT_STATUS_WORK_QUEUE_NAME),
		PAYMENT_ORDER_CALLBACK_MQ_DEF(DEF_PREFIX+"payment.order.callbackExchange",DEF_PREFIX+"payment.order.callbackRoute",PAYMENT_ORDER_CALLBACK_QUEUE_NAME);
		private  String exchangeName;
		private  String routeName;
		private  String queueName;

		MQDef(String exchangeName,String routeName,String queueName){
			this.exchangeName = exchangeName;
			this.routeName = routeName;
			this.queueName = queueName;
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
	}

	@Bean("callbackWorkExchange")
	public DirectExchange workExchange(){
		return new DirectExchange(CALLBACK_MQ_DEF.exchangeName,true,false);
	}

	@Bean("callbackWorkQueue")
	public Queue workQueue(){
		return new Queue(CALLBACK_MQ_DEF.queueName,true);
	}

	@Bean("callbackWorkBind")
	public Binding workBind(){
		return BindingBuilder.bind(workQueue()).to(workExchange()).with(CALLBACK_MQ_DEF.routeName);
	}

	@Bean("paymentStatusWorkExchange")
	public DirectExchange statusWorkExchange(){
		return new DirectExchange(PAYMENT_STATUS_WORK_MQ_DEF.exchangeName,true,false);
	}

	@Bean("paymentStatusWorkQueue")
	public Queue statusWorkQueue(){
		return new Queue(PAYMENT_STATUS_WORK_MQ_DEF.queueName,true);
	}

	@Bean("paymentStatusWorkBind")
	public Binding statusWorkBind(){
		return BindingBuilder.bind(statusWorkQueue()).to(statusWorkExchange()).with(PAYMENT_STATUS_WORK_MQ_DEF.routeName);
	}

	@Bean("paymentStatusDelayExchange")
	public DirectExchange statusDelayExchange(){
		return new DirectExchange(PAYMENT_STATUS_DELAY_MQ_DEF.exchangeName,true,false);
	}

	@Bean("paymentStatusDelayQueue")
	public Queue statusDelayQueue(){
		Map<String, Object> args = new HashMap<>(3);
		args.put("x-dead-letter-exchange", PAYMENT_STATUS_WORK_MQ_DEF.exchangeName);
		args.put("x-dead-letter-routing-key", PAYMENT_STATUS_WORK_MQ_DEF.routeName);
		// 10分钟延迟检测
		args.put("x-message-ttl", (long) 1000 * 60 * 10);
		return new Queue(PAYMENT_STATUS_DELAY_MQ_DEF.queueName, true, false, false, args);
	}

	@Bean("paymentStatusDelayBind")
	public Binding statusDelayBind(){
		return BindingBuilder.bind(statusDelayQueue()).to(statusDelayExchange()).with(PAYMENT_STATUS_DELAY_MQ_DEF.routeName);
	}

	@Bean("paymentOrderCallbackWorkExchange")
	public DirectExchange orderCallbackWorkExchange(){
		return new DirectExchange(PAYMENT_ORDER_CALLBACK_MQ_DEF.exchangeName,true,false);
	}

	@Bean("paymentOrderCallbackWorkQueue")
	public Queue orderCallbackWorkQueue(){
		return new Queue(PAYMENT_ORDER_CALLBACK_MQ_DEF.queueName,true);
	}

	@Bean("paymentOrderCallbackWorkBind")
	public Binding orderCallbackWorkBind(){
		return BindingBuilder.bind(orderCallbackWorkQueue()).to(orderCallbackWorkExchange()).with(PAYMENT_ORDER_CALLBACK_MQ_DEF.routeName);
	}

}
