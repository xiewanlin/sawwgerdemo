package com.xwl.learn.rabbit.provider;

import com.xwl.learn.param.TestParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiewanlin
 * @Date: 2020/5/20
 */
public class MqProvider {

  @Autowired
  protected RabbitTemplate rabbitTemplate;

  public void sendMq() {
    TestParam param = new TestParam();
    rabbitTemplate.convertAndSend("exchangeName","rountName", param);
  }

}
