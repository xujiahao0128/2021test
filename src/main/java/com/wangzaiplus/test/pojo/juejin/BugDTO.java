package com.wangzaiplus.test.pojo.juejin;

import lombok.*;

import java.io.Serializable;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年04月07日 11:33
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugDTO  implements Serializable {

    private Integer bugType;
    private Long bugTime;
    private Integer bugShowType;
    private Boolean isFirst;

}
