package com.wangzaiplus.test.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: MyAnnotation
 * @description: 自定义注解
 * @author: Mr.Xu
 * @time: 2021/6/2 10:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {

    /** 默认开启 */
    boolean enable() default true;
}
