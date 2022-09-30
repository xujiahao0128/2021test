package com.wangzaiplus.test.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailUtilTest {

    @Test
    public void send() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        List<String> collect = list.stream().filter(item -> item > 3).map(item -> item + "x").collect(Collectors.toList());
        System.out.println("collect = " + collect);
    }

    @Test
    public void sendAttachment() {
    }


}