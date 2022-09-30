package com.wangzaiplus.test.mongodb.service.impl;

import com.alibaba.fastjson.JSON;
import com.wangzaiplus.test.mongodb.entity.Comment;
import com.wangzaiplus.test.mongodb.repository.CommentRepository;
import com.wangzaiplus.test.mongodb.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年07月21日 15:37
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CommentRepository commentRepository;

    /*** 保存一个评论 * @param comment */
    @Override
    public void saveComment(Comment comment){
        //如果需要自定义主键，可以在这里指定主键；如果不指定主键，MongoDB会自动生成主键,设置一些默认初始值
        commentRepository.save(comment);
    }

    /*** 更新评论 * @param comment */
    @Override
    public void updateComment(Comment comment){
        commentRepository.save(comment);
    }

    /*** 根据id删除评论 * @param id */
    @Override
    public void deleteCommentById(String id){
        commentRepository.deleteById(id);
    }

    /*** 查询所有评论 * @return */
    @Override
    public List<Comment> findCommentList(){
        return commentRepository.findAll();
    }

    /*** 根据id查询评论 * @param id * @return */
    @Override
    public Comment findCommentById(String id){
        return commentRepository.findById(id).get();
    }

    @Override
    public List<Comment> findCommentListPageByParentid(String parentid, int page, int size) {
        Query query = new Query();
        Criteria criteria = Criteria.where("parentid").is(parentid);
        //下面条件相当于select * from order where orderStatus in(16,32) and payStatus=4 and finishTime<=2019-07-05 00:00:00 and (customerId=139 or customerId=1360)
        //criteria.and("orderStatus").in(16, 32).andOperator(Criteria.where("payStatus").is(4)).and("finishTime").lte("2019-07-05 00:00:00")
        //        .orOperator(Criteria.where("customerId").is("139"),Criteria.where("customerId").is("1360"));
        query.addCriteria(criteria);
        System.out.println(JSON.toJSONString(criteria));
        //计算总数,用于算法分页数
        long count = mongoTemplate.count(query, Comment.class);
        System.out.println(count);

        int pageNum=page;
        int pageSize=size;
        //总页数
        int pageTotal=(int) (count%pageSize==0?count/pageSize:count/pageSize+1);
        System.out.println(pageTotal);
        int offset=(pageNum-1)*pageSize;
        //排序逻辑
        query.with(Sort.by(Sort.Order.desc("userid")));
        // 分页逻辑
        query.skip(offset).limit(pageSize);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        return comments;
    }
}
