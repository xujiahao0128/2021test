package com.wangzaiplus.test.dto;

import com.wangzaiplus.test.annotation.MyAnnotation;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName: TestDTO
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2021/6/2 16:23
 */
@Data
@Builder
public class TestDTO {

    private String name;

    private int age;

    private boolean sex;

    @MyAnnotation
    public int test(){
        return this.getAge()+1;
    }
}
