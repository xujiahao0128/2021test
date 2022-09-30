package com.wangzaiplus.test.demoProxy;

/**
 * @ClassName: Court
 * @description: 打官司接口
 * @author: Mr.Xu
 * @time: 2020/12/7 15:29
 */
public interface Court {
    //定义一个方法 要求所有参与到打官司中的角色都有 打官司的功能
    int doCourt(int a);

    int speak();
}
