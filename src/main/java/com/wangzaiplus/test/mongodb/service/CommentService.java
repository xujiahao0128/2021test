package com.wangzaiplus.test.mongodb.service;

import com.wangzaiplus.test.mongodb.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年07月21日 15:37
 */
public interface CommentService {

    /*** 保存一个评论 * @param comment */
    public void saveComment(Comment comment);

    /*** 更新评论 * @param comment */
    public void updateComment(Comment comment);

    /*** 根据id删除评论 * @param id */
    public void deleteCommentById(String id);

    /*** 查询所有评论 * @return */
    public List<Comment> findCommentList();

    /*** 根据id查询评论 * @param id * @return */
    public Comment findCommentById(String id);

    /**
     * @Description 根据父id查询分页列表
     * @Date 2021/7/21 18:25
     * @param  * @param parentid
     * @param page
     * @param size
     * @return org.springframework.data.domain.Page<com.wangzaiplus.test.mongodb.entity.Comment>
     **/
    public List<Comment> findCommentListPageByParentid(String parentid,int page ,int size);
}
