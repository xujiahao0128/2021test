package com.wangzaiplus.test.mongodb.repository;

import com.wangzaiplus.test.mongodb.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xujiahao
 */
@Resource
@Component
public interface CommentRepository extends MongoRepository<Comment,String> {

    /**
     * @Description 根据父id，查询子评论的分页列表
     * @Date 2021/7/21 18:19
     * @param  * @param parentId:父id
     * @return org.springframework.data.domain.Page<com.wangzaiplus.test.mongodb.entity.Comment>
     **/
    @Query(value = "{'parentid':?0}")
    List<Comment> commentListByParentId(String parentid);
}
