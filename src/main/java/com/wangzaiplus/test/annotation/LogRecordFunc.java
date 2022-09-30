package com.wangzaiplus.test.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Mr.Xu
 * @Description: 声明自定义函数，方法和类都要加上这注解
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface LogRecordFunc {
}
