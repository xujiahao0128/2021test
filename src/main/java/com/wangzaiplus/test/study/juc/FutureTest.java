package com.wangzaiplus.test.study.juc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年06月27日 17:42
 */
public class FutureTest {

    @Test
    public void testFuture() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Future<Integer> future = executorService.submit(() -> {

                TimeUnit.SECONDS.sleep(10);
                int nextInt = ThreadLocalRandom.current().nextInt(10);
                return nextInt;
            });
            futures.add(future);
        }
        for (int i = 0; i < futures.size(); i++) {
            if (i == 0){
                futures.get(i).cancel(true);
            }
            if (futures.get(i).isDone() && !futures.get(i).isCancelled()) {
                System.out.println("future : " + futures.get(i).get());
            }
        }


    }

}
