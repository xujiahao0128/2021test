package com.wangzaiplus.test.mq;

import com.rabbitmq.client.Channel;
import com.wangzaiplus.test.common.Constant;
import com.wangzaiplus.test.pojo.MsgLog;
import com.wangzaiplus.test.service.MsgLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.cglib.proxy.Enhancer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

@Slf4j
public class BaseConsumerProxy {

    private Object target;

    private MsgLogService msgLogService;

    public BaseConsumerProxy(Object target, MsgLogService msgLogService) {
        this.target = target;
        this.msgLogService = msgLogService;
    }

    public Object getProxy() {
        // 参数1 类加载器
        ClassLoader classLoader = target.getClass().getClassLoader();
        // 参数2 被代理对象实现的所有的接口的字节码数组
        Class[] interfaces = target.getClass().getInterfaces();
        // JDK Proxy创建代理对象 增强被代理对象对象: Proxy.newProxyInstance 方法的三个参数
        Object proxy = Proxy.newProxyInstance(classLoader, interfaces, handler);
        return proxy;
    }
    // 参数3 执行处理器 用于定义方法的增强规则（加强后的方法）
    InvocationHandler handler =new InvocationHandler(){
        //当代理对象调用了接口中的任何一个方法 都会将该方法以method对象的形式传入 invoke方法
        //1. proxy  代理对象  2.method 被代理对象的方法  3.args 被代理对象 方法被调用时 传入的实参 数组 4.return null; 返回被增强方法的结果
        @Override
        public Object invoke(Object proxy1, Method method, Object[] args) throws IOException {
            Message message = (Message) args[0];
            Channel channel = (Channel) args[1];

            String correlationId = getCorrelationId(message);

            if (isConsumed(correlationId)) {// 消费幂等性, 防止消息被重复消费
                log.info("重复消费, correlationId: {}", correlationId);
                return null;
            }
            MessageProperties properties = message.getMessageProperties();
            long deliveryTag = properties.getDeliveryTag();

            try {
                Object result = method.invoke(target, args);// 真正消费的业务逻辑
                msgLogService.updateStatus(correlationId, Constant.MsgLogStatus.CONSUMED_SUCCESS);
                // 消费确认 deliveryTag:该消息的index multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息
                channel.basicAck(deliveryTag, false);
                return result;
            } catch (Exception e) {
                log.error("getProxy error", e);
                //消费拒绝 deliveryTag:消息投递的唯一标识，作用域为当前channel multiple：是否批量:将一次性ack所有小于deliveryTag的消息 requeue：被拒绝的是否重新入队列
                channel.basicNack(deliveryTag, false, true);
                return null;
            }
        }
    };

    /**
     * 获取CorrelationId
     *
     * @param message
     * @return
     */
    private String getCorrelationId(Message message) {
        String correlationId = null;

        MessageProperties properties = message.getMessageProperties();
        Map<String, Object> headers = properties.getHeaders();
        for (Map.Entry entry : headers.entrySet()) {
            String key = (String) entry.getKey();
            if (key.equals("spring_returned_message_correlation")) {
                correlationId = (String) entry.getValue();
            }
        }

        return correlationId;
    }

    /**
     * 消息是否已被消费
     *
     * @param correlationId
     * @return
     */
    private boolean isConsumed(String correlationId) {
        MsgLog msgLog = msgLogService.selectByMsgId(correlationId);
        if (null == msgLog || msgLog.getStatus().equals(Constant.MsgLogStatus.CONSUMED_SUCCESS)) {
            return true;
        }

        return false;
    }


}
