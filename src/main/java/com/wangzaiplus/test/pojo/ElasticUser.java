package com.wangzaiplus.test.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月15日 17:51
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Data
public class ElasticUser implements Serializable {
    private static final long SerialVersionUID = 1L;

    private Integer id;

    private String name;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date birthday;

    public ElasticUser(Integer id,String name){
        this.id=id;
        this.name=name;
    }
}
