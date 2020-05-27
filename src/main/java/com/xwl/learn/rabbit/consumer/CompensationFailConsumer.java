package com.xwl.learn.rabbit.consumer;

import com.rabbitmq.client.Channel;
import com.xwl.learn.config.MQConfig;
import com.xwl.learn.param.TestParam;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author xiewanlin
 * @version 1.0
 * @description 支付3次补偿后失败消息消费者
 * @date 2019/12/31 18:43
 **/
@Component
@Slf4j
public class CompensationFailConsumer {

	 @RabbitListener(queues = {MQConfig.FAILED_QUEUE_NAME})
	 public void execute(Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
		 TestParam testParam) throws Exception{
		 System.out.println(String.format("异步补偿最终失败，消息体：%s", testParam.toString()));
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
			log.error("确认补偿消费信息失败",e);
			channel.close();
			throw e;
		}
	}
}
