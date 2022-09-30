package com.wangzaiplus.test.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月21日 9:35
 */
@Data
public class Log implements Serializable {

    private Long id;

    /** 业务id */
    private Long businessId;

    /** 业务类型 */
    private String businessType;

    /** 操作类型 */
    private String operationType;

    /** 日志描述 */
    private String logDesc;

    /** 方法描述 */
    private String methodDesc;

    /** 错误描述 */
    private String errorMessage;

    /** 返回内容 */
    private String returnMessage;

    /** 是否执行成功 */
    private Boolean isSuccessful;

    /** 执行时间 */
    private Date operateDate;

    /** 执行时间 */
    private Long executionTime;

}
