package com.wangzaiplus.test.annotation;


import java.lang.annotation.*;

/**
 * @author MR.XU
 * @Description : 日志注解（可复合
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LogRecords.class)
public @interface LogRecord {

    /** 默认不开启日志记录（通过mq异步存储到数据库） */
    boolean enableMq() default false;

    /** 业务id */
    String businessId() default "";

    /** 业务类型 */
    String businessType() default "";

    /** 操作类型 */
    String operationType() default "";

    /** 日志描述 */
    String logDesc();

    /** 方法描述 */
    String methodDesc();

}
