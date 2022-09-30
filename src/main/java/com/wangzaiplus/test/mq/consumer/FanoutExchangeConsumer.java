package com.wangzaiplus.test.mq.consumer;

import com.rabbitmq.client.Channel;
import com.wangzaiplus.test.mq.BaseConsumer;
import com.wangzaiplus.test.pojo.Mail;
import com.wangzaiplus.test.pojo.MsgLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @ClassName: FanoutExchangeConsumer
 *
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2020/12/17 11:37
 */
@Component
@Slf4j
public class FanoutExchangeConsumer implements BaseConsumer {

  @Override
  public void consume(Message message, Channel channel) throws IOException {
    log.info("收到消息: {}", message.toString());
  }
}