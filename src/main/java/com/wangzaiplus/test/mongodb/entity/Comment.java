package com.wangzaiplus.test.mongodb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年07月21日 14:43
 */
/** 把一个java类声明为mongodb的文档，可以通过collection参数指定这个类对应的文档。 若未加 @Document ，该 bean save 到 mongo 的 comment collection
 *   若添加 @Document ，则 save 到 comment collection @Document(collection="comment")可以省略，如果省略，则默认使用类名小写映射集合
  */
@Data
@ToString
@Document(collection="comment")
// 复合索引
// @CompoundIndex( def = "{'userid': 1, 'nickname': -1}")
public class Comment {

    /** 主键标识，该属性的值会自动对应mongodb的主键字段"_id"，如果该属性名就叫“id”,则该注解可以省略，否则必须写 */
    @Id
    private String id;
    /** 该属性对应mongodb的字段的名字，如果一致，则无需该注解 吐槽内容*/
    @Field("content")
    private String content;
    /** 发布日期 */
    @Field("publishtime")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date publishTime;
    /** 添加了一个单字段的索引 发布人ID*/
    @Indexed
    @Field("userid")
    private String userId;
    /** 昵称 */
    @Field("nickname")
    private String nickName;
    /** 评论的日期时间 */
    @Field("createdatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createDateTime;
    /** 点赞数 */
    @Field("likenum")
    private Integer likeNum;
    /** 回复数 */
    @Field("replynum")
    private Integer replyNum;
    /** 状态 */
    @Field("state")
    private String state;
    /** 上级ID */
    @Field("parentid")
    private String parentId;
    /** 文章id */
    @Field("articleid")
    private String articleid;
}
