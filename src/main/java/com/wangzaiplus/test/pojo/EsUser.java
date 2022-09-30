package com.wangzaiplus.test.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月18日 14:26
 */
@Data
@ToString
@Document(indexName = "es_test")
@AllArgsConstructor
@NoArgsConstructor
public class EsUser implements Serializable {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    /** elasticsearch7.x 时间类型 yyyy需要变成uuuu, 年月日得localdate类型 年月日时分秒得localdatetime类型 */
    @Field(type = FieldType.Date , format = DateFormat.custom , pattern = "uuuu-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate createdDate;

    @Field(type = FieldType.Nested)
    private List<EsItem> esItemList;


}
