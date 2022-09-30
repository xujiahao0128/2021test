package com.wangzaiplus.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author Mr.Xu
 * @Description 查询es条件对象
 * @date 2021年09月16日 16:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryContentDTO implements Serializable {

    private String index="study_java";

    private String title;

    private Integer pageSize;

    private Integer pageNo;

}
