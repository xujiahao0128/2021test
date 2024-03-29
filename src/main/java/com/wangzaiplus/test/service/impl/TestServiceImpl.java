package com.wangzaiplus.test.service.impl;

import com.wangzaiplus.test.annotation.LogRecord;
import com.wangzaiplus.test.annotation.MyAnnotation;
import com.wangzaiplus.test.context.LogContext;
import com.wangzaiplus.test.dto.TestDTO;
import com.wangzaiplus.test.mq.MessageHelper;
import com.wangzaiplus.test.common.ResponseCode;
import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.config.RabbitConfig;
import com.wangzaiplus.test.mapper.MsgLogMapper;
import com.wangzaiplus.test.pojo.Mail;
import com.wangzaiplus.test.pojo.MsgLog;
import com.wangzaiplus.test.service.TestService;
import com.wangzaiplus.test.util.RandomUtil;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @MyAnnotation
    public ServerResponse test(TestDTO testDTO) {
        if (testDTO.getAge()>20){
            throw new RuntimeException();
        }
        return ServerResponse.success(testDTO.test());
    }

    @Override
    @LogRecord(enableMq = true,businessId = "#id",logDesc = "'修改值' +#old+ '为'+#id",methodDesc = "修改xxx")
    @LogRecord(enableMq = true,businessId = "#id",logDesc = "'修改值' +#func(#old)+ '为' +#id",methodDesc = "修改xxx")
    public ServerResponse testIdempotence(String id) {
    System.out.println("==============testIdempotence");
        LogContext.putVariables("old","666666");
        return ServerResponse.success("testIdempotence: success");
    }

    @Override
    public ServerResponse accessLimit() {
        return ServerResponse.success("accessLimit: success");
    }

    @Override
    public ServerResponse send(Mail mail) {
        String msgId = RandomUtil.UUID32();
        mail.setMsgId(msgId);

        MsgLog msgLog = new MsgLog(msgId, mail, RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME);
        msgLogMapper.insert(msgLog);// 消息入库

        CorrelationData correlationData = new CorrelationData(msgId);
        rabbitTemplate.convertAndSend(RabbitConfig.MAIL_EXCHANGE_NAME, RabbitConfig.MAIL_ROUTING_KEY_NAME, MessageHelper.objToMsg(mail), correlationData);// 发送消息

        return ServerResponse.success(ResponseCode.MAIL_SEND_SUCCESS.getMsg());
    }

}
