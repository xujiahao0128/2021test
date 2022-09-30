package com.wangzaiplus.test.annotation;

import java.lang.annotation.*;
import java.util.List;

/**
 * 在需要保证 接口防刷限流 的Controller的方法上使用此注解,放在类上代表该类的方法全部使用，method优先级高于class
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    int maxCount();// 最大访问次数

    int seconds();// 固定时间, 单位: s

    String [] ignoreMethod() default {""};//需要跳过的方法名

}
