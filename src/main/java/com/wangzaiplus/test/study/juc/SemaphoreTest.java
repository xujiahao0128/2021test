package com.wangzaiplus.test.study.juc;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Xu
 * @Description
 * ● Semaphore可以用来限制或管理数量有限的资源的使用情况
 * ● 比如控制排污企业，污染不能太多，可以给允许排放污染的企业颁发许可证
 * ●信号量的作用是维护一个“许可证”的计数，线程可以获取许可证，那信号量剩余的许可证就减1，线程也可以释放一个许可证，那信号量剩余的许可证就加1，
 * 当信号量所拥有的许可证数量为0，那么下一个还想要获取许可证的线程，就需要等待，直到有另外的线程释放了许可证
 * @date 2022年09月08日 16:03
 */
public class SemaphoreTest {


    @Test
    public void testSemaphore() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Semaphore semaphore = new Semaphore(3);

        AtomicInteger atomicInteger = new AtomicInteger(10);
        AtomicBoolean flag = new AtomicBoolean(true);

        while(flag.get()){
            executorService.submit(() ->{
                int i = atomicInteger.decrementAndGet();
                if (i < 1){
                    flag.set(false);
                }
                Thread thread = Thread.currentThread();
                String format = String.format("线程名称：%s，线程id：%s", thread.getName(), thread.getId());
                try {
                    System.out.println("当前等待队列数量：" + semaphore.getQueueLength());
                    //System.out.println(format + "==>获取令牌中~");
                    //获取令牌，未获取到则阻塞
                    semaphore.acquire();
                    //3秒没获取到令牌就退出，使用tryAcquire不会将当前线程新增到等待队列
                    //if (semaphore.tryAcquire(3, TimeUnit.SECONDS)) {
                        System.out.println(format + "==>获取到令牌，执行业务逻辑耗时5s");
                        TimeUnit.SECONDS.sleep(5);
                    /*}else {
                        System.out.println(format + "==>获取令牌失败，退出程序~");
                    }*/

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    System.out.println(format + "==>释放令牌~");
                    //释放令牌
                    semaphore.release();
                }
            });
        }



        TimeUnit.SECONDS.sleep(10);
        //多线程情况下获取等待队列方法是不准确的
        while(semaphore.getQueueLength() > 0){
            TimeUnit.SECONDS.sleep(5);
            System.out.println("当前等待队列数量：" + semaphore.getQueueLength());
        }
        System.out.println("==========任务以完成，当前等待队列数量" + semaphore.getQueueLength());
    }

}
