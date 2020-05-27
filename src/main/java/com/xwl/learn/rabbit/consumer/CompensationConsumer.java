package com.xwl.learn.rabbit.consumer;

import static com.xwl.learn.config.MQConfig.CompensationMQDef.FAILED_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.FIVE_MIN_DELAY_MQ_DEF;
import static com.xwl.learn.config.MQConfig.CompensationMQDef.TEN_MIN_DELAY_MQ_DEF;

import com.rabbitmq.client.Channel;
import com.xwl.learn.aop.idempotent.Idempotent;
import com.xwl.learn.config.MQConfig;
import com.xwl.learn.param.TestParam;
import com.xwl.learn.utils.MessageUtil;
import com.xwl.learn.utils.SpringContextHolder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 补偿工作队列消费者
 * @date 2019/12/31 18:43
 **/
@Component
@Slf4j
public class CompensationConsumer {

	 @Autowired
	 private RabbitTemplate rabbitTemplate;

	 @RabbitListener(queues = {MQConfig.WORK_QUEUE_NAME})
	 public void execute(Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
			 @Header(MessageUtil.MQ_RETRY_HEADER_KEY) Integer times,
				 TestParam testParam) throws Exception{
		 try {
			 SpringContextHolder.getBean(CompensationConsumer.class).execute(testParam,times);
			 this.ackMessage(channel,deliveryTag);
		 } catch (Exception e) {
			 throw e;
		 }
	 }


	@Idempotent(namespace="name", key = "#compensationDO.requestId", cacheTime=60*60)
	public<T> void execute(TestParam testParam,int times){
		boolean flag =false;
		try {
			flag = true;
		} catch (Exception e) {
			log.error("业务补偿发生异常，继续新的补偿",e);
		}
		if(!flag){
			this.sendDelayMQ(testParam,times);
		}
	}

	/**
	 * 确认消费消息
	 * @param channel 连接的channel
	 * @param deliveryTag 消息id
	 */
	private void ackMessage(Channel channel, long deliveryTag) throws Exception {
		try {
			channel.basicAck(deliveryTag, false);
		} catch (IOException e) {
			log.error("确认补偿消费信息失败",e);
			channel.close();
			throw e;
		}
	}

	/**
	 * 发送延迟消息
	 * @param testParam
	 * @param times
	 */
	private void sendDelayMQ(TestParam testParam,int times){
		switch (times){
			case 1:
				MessageUtil.sendDelayMessage(FIVE_MIN_DELAY_MQ_DEF,testParam,2);
				break;
			case 2:
				MessageUtil.sendDelayMessage(TEN_MIN_DELAY_MQ_DEF,testParam,3);
				break;
				default:
					this.sendFailedMQ(testParam);
		}
	}

	private void sendFailedMQ(TestParam testParam){
			rabbitTemplate.convertAndSend(FAILED_MQ_DEF.getExchangeName(),
					FAILED_MQ_DEF.getRouteName(),testParam);
	}
}
