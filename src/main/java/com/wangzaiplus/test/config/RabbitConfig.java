package com.wangzaiplus.test.config;

import com.wangzaiplus.test.common.Constant;
import com.wangzaiplus.test.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private MsgLogService msgLogService;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());

        // 消息是否成功发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息成功发送到Exchange");
                String msgId = correlationData.getId();
                //msgLogService.updateStatus(msgId, Constant.MsgLogStatus.DELIVER_SUCCESS);
            } else {
                log.info("消息发送到Exchange失败, 相关数据：{}, 失败原因: {}", correlationData, cause);
            }
        });

        // 触发setReturnCallback回调必须设置mandatory=true, 否则Exchange没有找到Queue就会丢弃掉消息, 而不会触发回调
        rabbitTemplate.setMandatory(true);
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
                                          //消息    回调码       回调文本  交换机     路由
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("注意这是失败回调ReturnCallback！！！消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
        });

        return rabbitTemplate;
    }

    @Bean//对message消息body的序列化形式
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange testDirectExchange() {
        return new DirectExchange("testDirectExchange");
    }

    @Bean
    Queue testRabbitmqQueue(){
        return new Queue("testRabbitmqQueue", true);
    }

    @Bean
    FanoutExchange testFanoutExchange() {
        return new FanoutExchange("testFanoutExchange");
    }

    @Bean//绑定队列和交换机
    public Binding testRabbitmqBinding() {
        return BindingBuilder.bind(testRabbitmqQueue()).to(testFanoutExchange());
    }

    /*登录日志*/
    public static final String LOGIN_LOG_QUEUE_NAME = "login.log.queue";
    public static final String LOGIN_LOG_EXCHANGE_NAME = "login.log.exchange";
    public static final String LOGIN_LOG_ROUTING_KEY_NAME = "login.log.routing.key";

    @Bean                                                                //持久化
    public Queue logUserQueue() {
        return new Queue(LOGIN_LOG_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange logUserExchange() {
        return new DirectExchange(LOGIN_LOG_EXCHANGE_NAME, true, false);
    }

    @Bean//绑定队列和交换机
    public Binding logUserBinding() {
        return BindingBuilder.bind(logUserQueue()).to(logUserExchange()).with(LOGIN_LOG_ROUTING_KEY_NAME);
    }

    // 发送邮件
    public static final String MAIL_QUEUE_NAME = "mail.queue";
    public static final String MAIL_EXCHANGE_NAME = "mail.exchange";
    public static final String MAIL_ROUTING_KEY_NAME = "mail.routing.key";

    @Bean
    public Queue mailQueue() {
        return new Queue(MAIL_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange mailExchange() {
        return new DirectExchange(MAIL_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with(MAIL_ROUTING_KEY_NAME);
    }

    // 发送日志
    public static final String LOG_QUEUE_NAME = "log.queue";
    public static final String LOG_EXCHANGE_NAME = "log.exchange";
    public static final String LOG_ROUTING_KEY_NAME = "log.routing.key";

    @Bean
    public Queue logQueue() {
        return new Queue(LOG_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange logExchange() {
        return new DirectExchange(LOG_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding logBinding() {
        return BindingBuilder.bind(logQueue()).to(logExchange()).with(LOG_ROUTING_KEY_NAME);
    }

}
