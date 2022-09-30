package com.wangzaiplus.test.pojo;

import lombok.*;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月16日 16:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Content implements Serializable {

    private String img;

    private String title;

    private String price;

    private String shopname;

    private Boolean flag;

}
