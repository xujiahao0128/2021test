package com.wangzaiplus.test.aspect;

import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.TestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MyAspect
 * @description: TODO
 * @author: Mr.Xu
 * @time: 2021/6/2 10:27
 */
@Aspect
@Component
public class MyAspect {

    /**执行顺序 around前 > before > around后(如果执行报错不会有) > after > afterThrowing(执行报错)/afterReturning(执行正常)  */

    @Pointcut("@annotation(com.wangzaiplus.test.annotation.MyAnnotation)")
    public void MyPointCut(){}



    /**@Before 注解指定的方法在切面切入目标方法之前执行，可以做一些 Log 处理，也可以做一些信息的统计，
     * 比如获取用户的请求 URL 以及用户的 IP 地址等等
     */
    @Before(value = "MyPointCut()")
    public void Before(JoinPoint joinPoint){
        System.out.println("这里是aop！当前进入Before方法");
        /** 获取签名 */
        Signature signature = joinPoint.getSignature();
        Class declaringType = signature.getDeclaringType();
        //类名
        String name = declaringType.getName();
        //全限定包名
        String declaringTypeName = signature.getDeclaringTypeName();

    }

    /** @After 注解和 @Before 注解相对应，指定的方法在切面切入目标方法之后执行，也可以做一些完成某方法之后的 Log 处理。 */
    @After(value = "MyPointCut()")
    public Object After(JoinPoint joinPoint){
        System.out.println("这里是aop！当前进入After方法");

        return "return After";
    }

  /**
   * @Around可以自由选择增强动作与目标方法的执行顺序，也就是说可以在增强动作前后，甚至过程中执行目标方法。
   * 这个特性的实现在于，调用ProceedingJoinPoint参数的procedd()方法才会执行目标方法。
   * @Around可以改变执行目标方法的参数值，也可以改变执行目标方法之后的返回值。
   */
    @Around(value = "MyPointCut()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("这里是aop！当前进入Around方法执行方法");
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length != 0){
            TestDTO testDTO=(TestDTO)args[0];
            testDTO.setName("aop修改");
            testDTO.setAge(21);
            args[0]=testDTO;
        }
        //可以修改参数
        return  joinPoint.proceed(args);
    }
    /** 当被切方法执行过程中抛出异常时，会进入 @AfterThrowing 注解的方法中执行，在该方法中可以做一些异常的处理逻辑。
     * 要注意的是 throwing 属性的值必须要和参数一致，否则会报错。该方法中的第二个入参即为抛出的异常。 */
    @AfterThrowing(value = "MyPointCut()",throwing = "throwable")
    public Object AfterThrowing(JoinPoint joinPoint,Throwable throwable){
        System.out.println("这里是aop！当前进入AfterThrowing方法"+throwable.getMessage());
        return "return AfterThrowing";
    }

    /** @AfterReturning 注解和 @After 有些类似，区别在于 @AfterReturning 注解可以用来捕获切入方法执行完之后的返回值，
     * 对返回值进行业务逻辑上的增强处理 */
    @AfterReturning(value = "MyPointCut()",returning="result")
    public Object AfterReturning(JoinPoint joinPoint,Object result){
        System.out.println("这里是aop！当前进入AfterReturning方法");
        /** result是返回结果可以操作 ,可以修改返回值*/
        if(result instanceof ServerResponse){
            ServerResponse data=(ServerResponse)result;
            Object data1 = data.getData();
            System.out.println(data1);
            data.setData("修改结果会不会改变返回值？");
        }
        System.out.println(result);
        return "return AfterReturning";
    }

}

