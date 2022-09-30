package com.wangzaiplus.test.controller;

import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.pojo.User;
import com.wangzaiplus.test.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@EnableAsync
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;



    @GetMapping("users")
    public String getAll() {
        long start = System.currentTimeMillis();
        System.out.println("controller:"+Thread.currentThread().getName());
        String testLock = "";
                userService.getAll();
        long end = System.currentTimeMillis();
        return testLock+"耗时："+(end-start)+"毫秒";
    }

    @GetMapping("{id}")
    public String getOne(@PathVariable Integer id) {
        User user = userService.getOne(id);
        if (null != user) {
            return user.toString();
        } else {
            return "not exists";
        }
    }

    @PostMapping
    public String add(User user) {
        userService.add(user);
        return "nice";
    }

    @PutMapping
    public String update(User user) {
        userService.update(user);
        return "nice";
    }

    @DeleteMapping("{id}")
    public String delete(@PathVariable Integer id) {
        userService.delete(id);
        return "nice";
    }

    @PostMapping("login")
    public ServerResponse login(String username, String password) {
        return userService.login(username, password);
    }
    @GetMapping("loginOut/{userName}")
    public ServerResponse loginOut(@PathVariable(value = "userName",required = true)String username) {
        return userService.loginOut(username);
    }


}
