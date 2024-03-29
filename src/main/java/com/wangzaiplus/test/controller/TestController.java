package com.wangzaiplus.test.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.wangzaiplus.test.annotation.AccessLimit;
import com.wangzaiplus.test.annotation.ApiIdempotent;
import com.wangzaiplus.test.annotation.MyAnnotation;
import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.TestDTO;
import com.wangzaiplus.test.mapper.MsgLogMapper;
import com.wangzaiplus.test.mapper.UserMapper;
import com.wangzaiplus.test.pojo.Mail;
import com.wangzaiplus.test.pojo.User;
import com.wangzaiplus.test.service.TestService;
import com.wangzaiplus.test.service.batch.mapperproxy.MapperProxy;
import com.wangzaiplus.test.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/test")
@Slf4j
@AccessLimit(maxCount = 5, seconds = 5, ignoreMethod = {"accessLimit2"})
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MsgLogMapper msgLogMapper;

    //@ApiIdempotent
    @PostMapping("/testIdempotence")
    public ServerResponse testIdempotence(@RequestParam(value = "id")String id) {
        return testService.testIdempotence(id);
    }

    @PostMapping("/accessLimit")
    public ServerResponse accessLimit() {
        return testService.accessLimit();
    }

    @PostMapping("/accessLimit2")
    public ServerResponse accessLimit2() {
        return testService.accessLimit();
    }

    @PostMapping(value = "send", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)//, Errors errors
    public ServerResponse sendMail(@RequestParam Mail mail) {
    /*if (errors.hasErrors()) {
        String msg = errors.getFieldError().getDefaultMessage();
        return ServerResponse.error(msg);
    }*/
    System.out.println(mail.toString());
        return testService.send(mail);
    }

    @PostMapping("single")
    public ServerResponse single(int size) {
        List<User> list = Lists.newArrayList();

        for (int i = 0; i < size; i++) {
            String str = RandomUtil.UUID32();
            User user = User.builder().username(str).password(str).build();
            list.add(user);
        }

        long startTime = System.nanoTime();
        log.info("batch insert costs: {} ms", (System.nanoTime() - startTime) / 1000000);

        return ServerResponse.success();
    }

    @PostMapping("batchInsert")
    public ServerResponse batchInsert(int size) {
        List<User> list = Lists.newArrayList();

        for (int i = 0; i < size; i++) {
            String str = RandomUtil.UUID32();
            User user = User.builder().username(str).password(str).build();
            list.add(user);
        }

        new MapperProxy<User>(userMapper).batchInsert(list);

        return ServerResponse.success();
    }

    @PostMapping("batchUpdate")
    public ServerResponse batchUpdate(String ids) {
        List<User> list = Lists.newArrayList();

        String[] split = ids.split(",");
        for (String id : split) {
            User user = User.builder().id(Integer.valueOf(id)).username("batchUpdate_" + RandomUtil.UUID32()).password("123456").build();
            list.add(user);
        }

        new MapperProxy<User>(userMapper).batchUpdate(list);

        return ServerResponse.success();
    }

    @PostMapping("sync")
    public ServerResponse sync() {
        List<User> list = Lists.newArrayList();
        for (int i = 0; i < 300; i++) {
            String uuid32 = RandomUtil.UUID32();
            User user = User.builder().username(uuid32).password(uuid32).password2(uuid32).password3(uuid32)
                    .password4(uuid32).password5(uuid32).password6(uuid32).password7(uuid32)
                    .password8(uuid32).password9(uuid32).password10(uuid32).build();
            list.add(user);
        }

        userMapper.batchInsert(list);

        check(list);

        return ServerResponse.success();
    }

    @PostMapping("MyTest")
    public ServerResponse MyTest(@RequestBody TestDTO testDTO) {
        System.out.println("MyTest");
        return testService.test(testDTO);
    }

    @GetMapping("/deadFor")
    public ServerResponse deadFor() throws InterruptedException {

        while(true){

        }
    }

    @Async
    public void check(List<User> list) {
        String username = list.get(list.size() - 1).getUsername();
        User user = userMapper.selectByUsername(username);
        log.info(user.getUsername());
    }

}
