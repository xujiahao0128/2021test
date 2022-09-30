package com.wangzaiplus.test.controller;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: TestRabbitmqController
 * @description: 测试rabbitmq
 * @author: Mr.Xu
 * @time: 2020/12/17 9:44
 */
@RestController
@RequestMapping("/rabbitmq")
public class TestRabbitmqController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/TestMessageAck")//并没有创建该交换机，mq找不到交换机，测试回调ConfirmCallback ack
    public String TestMessageAck() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRouting", map);
        return "ok";
    }
    @GetMapping("/TestMessageAck2")//创建了该交换机但是并没有绑定队列，mq找不到队列，测试回调ReturnCallback ack
    public String TestMessageAck2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: lonelyDirectExchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>(5);
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        System.out.println(">>>"+map.size()+map.entrySet().size());
        rabbitTemplate.convertAndSend("testDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

    @GetMapping("/rabbitMq")
    public void rabbitMq(){
        String randomNumber = new String(UUID.randomUUID().toString());
        CorrelationData correlationData = new CorrelationData(randomNumber);
        rabbitTemplate.convertAndSend("testFanoutExchange",null,randomNumber,correlationData);
        System.out.println("发送出去的："+randomNumber);
    }

}
