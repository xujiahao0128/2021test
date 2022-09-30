package com.wangzaiplus.test.annotation;

import java.lang.annotation.*;

/**
 * @author Mr.Xu
 * @Description : 复合注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecords {

    /** 注意变量名必须为value，坑逼 */
    LogRecord[] value();

}
