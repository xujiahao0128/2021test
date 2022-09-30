package com.wangzaiplus.test.demoProxy;

/**
 * @ClassName: Person
 * @description: 张三打官司（被代理的人的实现类）
 * @author: Mr.Xu
 * @time: 2020/12/7 15:30
 */
public class Person implements Court{
    private String name;

    //有参  无参构造方法
    public Person() {
    }
    public Person(String name) {
        this.name = name;
    }

    //getter和setter器
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //重写接口doCourt speak两个方法
    @Override
    public int doCourt( int a) {
        System.out.println(name+"说:我没杀人");
        return a*10;
    }
    @Override
    public int speak() {
        return 5;
    }

    //定义一个eat方法
    public void eat(){

    }
}

