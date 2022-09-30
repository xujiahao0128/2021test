package com.wangzaiplus.test.demoProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName: TestProxy
 * @description: 前言： 张三作为被告被告杀人，张三申述自己没有杀人， 可是自己不懂法，便找来律师作为代理为自己辩护。
 * @author: Mr.Xu
 * @time: 2020/12/7 15:30
 */
//=========================== 律师替张三打官司（jdk动态代理对象）======================

public class TestProxy {
    public static void main(String[] args) {
        // 张三请律师打官司
        Person person=new Person("张三");
        // JDK Proxy(代理对象): Proxy.newProxyInstance 方法的三个参数
        //创建代理对象 增强 person对象 使用代理对象代替person 去执行 doCourt方法
        // 参数1 类加载器
        ClassLoader classLoader = person.getClass().getClassLoader();
        // 参数2 被代理对象实现的所有的接口的字节码数组 {Court.class , ... , ...};
        Class[] interfaces =person.getClass().getInterfaces();
        //Class[] interfaces={Court.class};
        // 参数3 执行处理器 用于定义方法的增强规则（加强后的方法）
        InvocationHandler handler =new InvocationHandler(){
            //当代理对象调用了接口中的任何一个方法 都会将该方法以method对象的形式传入 invoke方法
            //1. proxy  代理对象  2.method 被代理对象的方法  3.args 被代理对象 方法被调用时 传入的实参 数组 4.return null; 返回被增强方法的结果
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 当执行 被代理的方法 是  doCourt 的时候 增强 如果是其他方法 让其正常执行即可
                Object res =null;
                if(method.getName().equals("doCourt")){
                    System.out.println("律师取证:证据证明,案发当时 当事人在 酒店 谈一个项目");
                    res =method.invoke(person,args);// 让被代理对象原有的方法执行
                    System.out.println("证据显示:当事人不具备作案时间,不可能去杀人");// 律师添加其他的增强代码
                }else{                              // 其他方法 执行原有方法，不加强  例如：speak
                    res =method.invoke(person,args);
                }
                return res;// 返回运行结果
            }
        };

        Court lawyer = (Court) Proxy.newProxyInstance(classLoader,interfaces,handler);
        // 使用 代理对象 代替person对象去完成打官司的功能 代理对象lawyer调用接口方法
        int x =lawyer.doCourt(10);
        //10
        System.out.println(x);
    }
}

// 总结
// 1.在不修改原有代码的 或者没有办法修改原有代码的情况下  增强对象功能
//   使用代理对象 代替原来的对象去完成功能  进而达到拓展功能的目的
// 2.JDK Proxy 动态代理面向接口的动态代理
//   2.1 一定要有接口和实现类的存在 代理对象增强的是实现类 在实现接口的方法重写的方法
//   2.2 生成的代理对象只能转换成 接口的类型 不能转换成 被代理对象
//       例如：Court lawyer = (Court)Proxy.newProxyInstance(classLoader,interfaces,handler);
//       不能写成：Person lawyer = (Person)Proxy.newProxyInstance(classLoader,interfaces,handler);
//   2.3 代理对象只能增强接口中定义的方法  实现类中其他和接口无关的方法是无法增强的
//   2.4 代理对象只能读取到接口中方法上的注解 不能读取到实现类方法上的注解
