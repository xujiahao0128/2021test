package com.wangzaiplus.test.func;

import com.wangzaiplus.test.annotation.LogRecordFunc;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年01月25日 17:23
 */
@LogRecordFunc
public class MyFunc {

    @LogRecordFunc
    public static String func(String id){
    System.out.println("进入自定义函数");
        return "自定义函数："+id;
    }

}
