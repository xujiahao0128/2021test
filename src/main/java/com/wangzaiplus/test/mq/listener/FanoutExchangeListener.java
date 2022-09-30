package com.wangzaiplus.test.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

/**
 * @ClassName: FanoutExchangeListener
 *
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2020/12/17 9:27
 */
@Component
@RabbitListener(queues = "testRabbitmqQueue")
public class FanoutExchangeListener {

  HashSet hashSet = new HashSet();
  //@RabbitListener 的 bindings 属性声明 Binding（若 RabbitMQ 中不存在该绑定所需要的 Queue、Exchange、RouteKey
  // 则自动创建，若存在则抛出异常）
  @RabbitHandler
  public void handle(@Payload String msg, Channel channel, @Headers Map<String, Object> headers)
      throws IOException {
    // 获取到AMQP信道中的消息的唯一编号TagId，方便返回应答模式告知消息已接到
    Long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
    try {
      // 应答模式确认接收到消息,后面那个参数如果是false，就是只确认签收当前消息，如果是true，则签收全部比当前TagId小的消息
      channel.basicAck(tag, false);
      System.out.println(msg);
      hashSet.add(msg);
      System.out.println(hashSet);
      System.out.println("请求了" + hashSet.size());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(e);
      channel.basicNack(tag, false, true);
    }
  }


  //1.@RabbitListener 可以标注在类上面，需配合 @RabbitHandler 注解一起使用,也可以单独使用在方法上，不需要@RabbitHandler配合
  //2.@RabbitListener 标注在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，具体使用哪个方法处理，
  //根据 MessageConverter 转换后的参数类型

/*  @RabbitHandler
  public void handle1(String type1){
    System.out.println("业务逻辑1~~~~~");
  }
  @RabbitHandler
  public void handle2(String type2){
    System.out.println("业务逻辑2~~~~~");
  }*/
}
