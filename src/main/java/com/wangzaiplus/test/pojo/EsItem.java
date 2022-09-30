package com.wangzaiplus.test.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月18日 15:24
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EsItem implements Serializable {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String desc;

    @Field(type = FieldType.Date , format = DateFormat.custom , pattern = "uuuu-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate createdDate;

}
