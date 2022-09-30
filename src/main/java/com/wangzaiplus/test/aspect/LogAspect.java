package com.wangzaiplus.test.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wangzaiplus.test.annotation.LogRecord;
import com.wangzaiplus.test.config.RabbitConfig;
import com.wangzaiplus.test.context.CustomFunctionRegistrar;
import com.wangzaiplus.test.context.LogContext;
import com.wangzaiplus.test.dto.LogDTO;
import com.wangzaiplus.test.listen.SystemLogListen;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Xu
 * @Description 日志切面
 * @date 2022年01月21日 9:58
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SystemLogListen systemLogListen;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /** spel表达式解析器 */
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /** 参数名称解析器 */
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(com.wangzaiplus.test.annotation.LogRecord) || @annotation(com.wangzaiplus.test.annotation.LogRecords)")
    public void LogRecordPointCut(){}


    @Around(value = "LogRecordPointCut()")
    public Object LogRecordAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object obj = null;
        /** 日志集合 */
        List<LogDTO> logDTOS = new ArrayList<>();
        /** 记录执行时间 */
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            obj = joinPoint.proceed();
            logDTOS = resolveExpress(joinPoint, obj);
        } catch (Throwable throwable) {
            // 方法执行异常，自定义上下文未写入，但是仍然需要初始化其他变量
            logDTOS = resolveExpress(joinPoint, null);
            // logDTO写入异常信息
            logDTOS.forEach(logDTO -> {
                logDTO.setIsSuccessful(false);
                logDTO.setErrorMessage(throwable.getMessage());
            });
            throw throwable;
        }finally{
            stopWatch.stop();
            logDTOS.forEach(logDTO -> {
                try {
                    // 记录执行时间（毫秒级别）
                    logDTO.setExecutionTime(stopWatch.getTotalTimeMillis());
                    // 发送本地监听
                    if (systemLogListen != null) {
                        systemLogListen.logEvent(logDTO);
                    }
                    // 发送数据管道
                    if (rabbitTemplate != null) {
                        rabbitTemplate.convertAndSend(RabbitConfig.LOG_EXCHANGE_NAME,RabbitConfig.LOG_ROUTING_KEY_NAME,logDTO);
                    }
                } catch (Throwable throwable) {
                    log.error("SystemLogAspect doAround send log error", throwable);
                }
            });
            // 清除变量上下文
            LogContext.clearContext();
        }
        return obj;
    }

    public List<LogDTO> resolveExpress(ProceedingJoinPoint joinPoint,Object obj){
        try{
            List<LogDTO> logDTOList = new ArrayList<>();
            /** 获取入参 */
            Object[] arguments = joinPoint.getArgs();
            /** 获取切入方法 */
            Method method = getMethod(joinPoint);
            /** 方法注解 */
            LogRecord [] logRecords = method.getAnnotationsByType(LogRecord.class);

            for (LogRecord logRecord : logRecords) {
                String businessIdSpel = logRecord.businessId();
                String businessType = logRecord.businessType();
                String logDescSpel = logRecord.logDesc();
                boolean enableMq = logRecord.enableMq();
                String methodDesc = logRecord.methodDesc();
                String operationType = logRecord.operationType();
                String returnMsg = null;
                LogDTO logDTO = new LogDTO();
                logDTOList.add(logDTO);
                try{
                    //获取入参名称
                    String[] parameterNames = discoverer.getParameterNames(method);
                    //线程上下文
                    StandardEvaluationContext context = LogContext.getContext();
                    CustomFunctionRegistrar.register(context);
                    //方法参数不为空，键值对存入上下文
                    if (parameterNames != null) {
                        for (int len = 0; len < parameterNames.length; len++) {
                            context.setVariable(parameterNames[len], arguments[len]);
                        }
                    }
                    // 解析业务id
                    if (StringUtils.isNotBlank(businessIdSpel)) {
                        Expression bizIdExpression = parser.parseExpression(businessIdSpel);
                        businessIdSpel = bizIdExpression.getValue(context, String.class);
                    }
                    // 解析日志描述
                    if (StringUtils.isNotBlank(logDescSpel)) {
                        Expression msgExpression = parser.parseExpression(logDescSpel);
                        Object msgObj = msgExpression.getValue(context, Object.class);
                        logDescSpel = JSON.toJSONString(msgObj, SerializerFeature.WriteMapNullValue);
                    }
                    // returnObj 处理
                    returnMsg = JSON.toJSONString(obj);

                }catch (Exception e){
                    log.error("SystemLogAspect resolveExpress error", e);
                }finally{
                    logDTO.setBusinessId(Long.valueOf(businessIdSpel));
                    logDTO.setBusinessType(businessType);
                    logDTO.setEnableMq(enableMq);
                    logDTO.setLogDesc(logDescSpel);
                    logDTO.setMethodDesc(methodDesc);
                    logDTO.setOperationType(operationType);
                    logDTO.setOperateDate(new Date());
                    logDTO.setReturnMessage(returnMsg);
                    logDTO.setIsSuccessful(true);
                    logDTO.setMethodName(method.getName());
                }

            }
            return logDTOList;
        }catch (Exception e){
            log.error("SystemLogAspect resolveExpress error", e);
            return Collections.emptyList();
        }
    }


    /** 获取切面方法 */
    protected Method getMethod(JoinPoint joinPoint) {
        Method method = null;
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature ms = (MethodSignature) signature;
            Object target = joinPoint.getTarget();
            method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error("SystemLogAspect getMethod error", e);
        }
        return method;
    }

}
