package com.wangzaiplus.test.mq.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wangzaiplus.test.config.RabbitConfig;
import com.wangzaiplus.test.dto.LogDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月25日 17:15
 */
@Slf4j
@Component
public class LogListen {

    @RabbitListener(queues = RabbitConfig.LOG_QUEUE_NAME)
    public void listenerLog(Message message, Channel channel, LogDTO logDTO) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        log.info("接收到mq日志消息：{}",logDTO);
    }

}
