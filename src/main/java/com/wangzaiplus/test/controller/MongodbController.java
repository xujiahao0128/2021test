package com.wangzaiplus.test.controller;

import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.FundDto;
import com.wangzaiplus.test.mongodb.entity.Comment;
import com.wangzaiplus.test.mongodb.service.CommentService;
import com.wangzaiplus.test.util.FundUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年07月21日 16:03
 */
@RestController
@RequestMapping("/mongo")
@Slf4j
public class MongodbController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/saveComment")
    public ServerResponse saveComment(@RequestBody Comment comment) {
        commentService.saveComment(comment);
        return ServerResponse.success();
    }

    @GetMapping("/findCommentById/{id}")
    public ServerResponse findCommentById(@PathVariable(value = "id")String id) {
        return ServerResponse.success(commentService.findCommentById(id));
    }

    @GetMapping("/findComments")
    public ServerResponse findCommentById() {
        return ServerResponse.success(commentService.findCommentList());
    }

    @PostMapping("/updateComment")
    public ServerResponse updateComment(@RequestBody Comment comment) {
        commentService.updateComment(comment);
        return ServerResponse.success();
    }

    @PostMapping("/deleteCommentById")
    public ServerResponse deleteCommentById(@PathVariable(value = "id")String id) {
        commentService.deleteCommentById(id);
        return ServerResponse.success();
    }

    @PostMapping("/pageByParentId")
    public ServerResponse pageByParentId(@RequestParam(value = "page")int page,@RequestParam(value = "size")int size) {
        List<Comment> pageComments = commentService.findCommentListPageByParentid("100001", page, size);
        return ServerResponse.success(pageComments);
    }

}
