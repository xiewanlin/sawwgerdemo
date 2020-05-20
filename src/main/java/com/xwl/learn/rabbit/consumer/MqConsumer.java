package com.xwl.learn.rabbit.consumer;

import com.rabbitmq.client.Channel;
import com.xwl.learn.config.MQCommonConfig;
import com.xwl.learn.param.TestParam;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqConsumer {

	 @RabbitListener(queues = {MQCommonConfig.CALLBACK_QUEUE_NAME})
	 public void excute(Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
			 TestParam param) throws Exception{
		 try {
		 }catch(Exception e){
			 throw e;
		 }
		 this.ackMessage(channel,deliveryTag);
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
			log.error("确认通知回调信息失败",e);
			channel.close();
			throw e;
		}
	}
}
