package com.wangzaiplus.test.dto;

import com.wangzaiplus.test.pojo.Log;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月20日 17:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class LogDTO extends Log implements Serializable {

    /** 全限定方法名称 */
    private String methodName;

    /** 是否开启mq */
    private Boolean enableMq;

}
