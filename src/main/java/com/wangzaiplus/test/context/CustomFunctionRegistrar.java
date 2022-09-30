package com.wangzaiplus.test.context;

import com.wangzaiplus.test.annotation.LogRecordFunc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月25日 15:04
 */
@Slf4j
@Data
@Component
public class CustomFunctionRegistrar implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /** 获取所有自定义函数方法容器 */
    private static List<Method> functions = new ArrayList<>();

    /**
     * 扫描申明的自定义函数，在填充普通bean属性之后，在初始化回调之前调用
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        /** 获取有自定义函数注解对象 */
        Map<String, Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(LogRecordFunc.class);
        beanWithAnnotation.values()
                .forEach(
                        component -> {
                            Method[] methods = component.getClass().getMethods();
                            Arrays.stream(methods)
                                    .filter(method -> method.isAnnotationPresent(LogRecordFunc.class))
                                    .forEach(m -> {
                                        functions.add(m);
                                        log.info("LogRecord register custom function [{}]", m);
                                    });
                        }
                );
    }

    /** 将自定义函数载入到当前传入的上下文 */
    public static void register(StandardEvaluationContext context) {
        functions.forEach(m -> context.registerFunction(m.getName(), m));
    }


}
