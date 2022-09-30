package com.wangzaiplus.test.study.juc;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年07月05日 16:26
 */
public class ThreadLocalTest {

    @Test
    public void testThreadLocal() throws BrokenBarrierException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {


        ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> {return 400;});

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            threadLocal.set(800);
            Integer integer = threadLocal.get();
            System.out.println("threadLocal.get()1 = " + integer);
            return integer;
        });

        executorService.submit(() -> {
            threadLocal.set(800);
            Integer integer = threadLocal.get();
            System.out.println("threadLocal.get()2 = " + integer);
            return integer;
        });

        threadLocal.set(1000);


    }

  public static void main(String[] args) {
      ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> {return 400;});

      ExecutorService executorService = Executors.newFixedThreadPool(2);

      CompletableFuture.supplyAsync(() -> {
          threadLocal.set(800);
          Integer integer = threadLocal.get();
          System.out.println("threadLocal.get()1 = " + integer);
          return integer;
      },executorService).thenRunAsync(() -> {
          threadLocal.set(800);
          Integer integer = threadLocal.get();
          System.out.println("threadLocal.get()2 = " + integer);
      });

      threadLocal.set(1000);
  }

}


