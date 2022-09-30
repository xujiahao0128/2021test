package com.wangzaiplus.test.pojo.juejin;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Xu
 * @Description 掘金签到返回信息实体
 * @date 2021年10月23日 14:46
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Check implements Serializable {

    private static final long SerialVersionUID = 1L;

    private Integer err_no;

    private String err_msg;

    private Map<String,Object> data = new HashMap<>();

}
