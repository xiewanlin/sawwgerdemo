package com.xwl.learn.utils;

import com.xwl.learn.config.MQConfig.CompensationMQDef;
import java.util.Objects;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 消息发送工具类
 * @date 2020/1/2 11:07
 **/
public class MessageUtil {

	/**
	 * 消息重试次数头信息
	 */
	public static final String MQ_RETRY_HEADER_KEY = "x-retry-times";

	/**
	 * 发送到异步补偿延迟队列中
	 * @param mqDef
	 * @param param
	 * @param retryTimes
	 */
	public static void sendDelayMessage(CompensationMQDef mqDef,Object param,Integer retryTimes){
		SpringContextHolder.getBean(RabbitTemplate.class).convertAndSend(mqDef.getExchangeName(),
				mqDef.getRouteName(), param,getCommonDelayProcessor(retryTimes,mqDef.getDelaySecs()));
	}

	private static MessagePostProcessor getCommonDelayProcessor(Integer times,Integer delaySecs){
		return new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				MessageProperties messageProperties = message.getMessageProperties();
				messageProperties.setHeader(MQ_RETRY_HEADER_KEY,times);
				if (Objects.nonNull(delaySecs)) {
					messageProperties.setExpiration(String.valueOf(delaySecs * 1000));
				}
				return message;
			}
		};
	}
}
