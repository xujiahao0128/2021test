package com.wangzaiplus.test.demoProxy;

import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName: CgilbProxy
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2020/12/23 15:06
 */
public class CGLibProxy implements MethodInterceptor {

    private Speak target;

    public CGLibProxy(Speak speak){
        this.target=speak;
    }
    public CGLibProxy(){
    }
    
    public Speak CreateProxy(){
        //声明增强类实例,用于生产代理类
        Enhancer enhancer = new Enhancer();
        //设置代理类字节码，CGLIB根据字节码生成被代理类的子类
        enhancer.setSuperclass(target.getClass());
        //设置回调函数，实际上是通过方法拦截进行增强
        enhancer.setCallback(this);
        //创建代理对象
        return (Speak) enhancer.create();
    }

    /**
     * 回调函数
     * @param proxy 代理对象
     * @param method 委托类方法
     * @param args 方法参数
     * @param methodProxy 每个被代理的方法都对应一个MethodProxy对象，
     *                    methodProxy.invokeSuper方法最终调用委托类(目标类)的原始方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //当代理对象调用方法为spe时才发起增强
        if(method.getName().equals("spe")){
            System.out.println("this is CGLibProxy 这是增强前！参数未修改前："+args[0]);
            args[0]="参数被修改！";
            Object result = methodProxy.invokeSuper(proxy, args);
            System.out.println("this is CGLibProxy 这是增强后！参数修改后："+args[0]);
            return result;
        }
        //不是该方法则不做任何操作
        return methodProxy.invokeSuper(proxy,args);
    }


    /**
     * 设置保存Cglib代理生成的类文件。
     *
     * @throws Exception
     */
    public static void saveGeneratedCGlibProxyFiles(String dir) throws Exception {
        Field field = System.class.getDeclaredField("props");
        field.setAccessible(true);
        Properties props = (Properties) field.get(null);
        //dir为保存文件路径
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, dir);
        props.put("net.sf.cglib.core.DebuggingClassWriter.traceEnabled", "true");
    }

    public static void main(String[] args) throws Exception {
        /** 开启 保存cglib生成的动态代理类类文件*/
        saveGeneratedCGlibProxyFiles(System.getProperty("user.dir"));
        CGLibProxy cgLibProxy = new CGLibProxy(new Speak());
        Speak speak = cgLibProxy.CreateProxy();
        speak.spe("徐家豪");
    }
}


