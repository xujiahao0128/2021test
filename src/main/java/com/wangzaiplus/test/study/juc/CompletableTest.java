package com.wangzaiplus.test.study.juc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年06月22日 17:47
 */
@Slf4j
public class CompletableTest {

    /**
     * 凡是有Async的都是异步执行且可指定线程池，对应的有Async的方法就有对应的非异步的方法
     * runAsync : 执行无返回值
     * supplyAsync : 执行有返回值
     * thenAcceptAsync : 依赖上一步的结果当做参数执行下一步
     * acceptEitherAsync : 传入一个其他的completableFuture任务、和当前任务比较，返回执行较快任务的结果,无返回
     * applyToEitherAsync : 传入一个其他的completableFuture任务、和当前任务比较，返回执行较快任务的结果,有返回
     * exceptionally : 当之前的任务执行发送异常将当前异常当做参数执行当前步骤
     * handleAsync : 不管之前的任务是否发生异常，都将上个任务和异常当做参数传入当前任务
     * runAfterBothAsync : 传入一个任务，只有当前任务和传入任务都执行成功才执行指定步骤
     * runAfterEitherAsync : 传入一个任务，只要当前任务和传入任务,先执行完成且成功，那么就执行自定义操作，即使执行慢的任务异常也不会影响自定义操作
     * thenAcceptAsync : 当上一阶段执行成功，则将上阶段返回结果当做参数进入下一阶段，如果上一阶段报错则不会执行下一阶段，无返回值
     * thenApplyAsync :  当上一阶段执行成功，则将上阶段返回结果当做参数进入下一阶段，如果上一阶段报错则不会执行下一阶段 ,有返回值
     * thenAcceptBothAsync : 当前任务和传入任务都执行成功才进入下一个阶段，无返回
     * thenCombineAsync : 当前任务和传入任务都执行成功才进入下一个阶段，有返回
     * thenComposeAsync : 接收上一个任务的结果作为当前阶段的参数，返回一个completableFuture任务，该任务和执行当前阶段任务（thenComposeAsync）的线程不是一个
     * thenRunAsync: 上一个任务执行成功则执行当前任务
     * whenCompleteAsync: 上一个任务执行结果和异常作为下一步任务的参数
     * CompletableFuture.allOf：CompletableFuture.allOf会等待所有传入任务全部执行完成，前提是调用get或者join
     * anyOf：CompletableFuture.anyOf任务集合执行最快的那一个执行完成就结束且结果等于该任务结果
     * */
    @Test
    public void anyOf() throws ExecutionException, InterruptedException {
        //anyOf：CompletableFuture.anyOf任务集合执行最快的那一个执行完成就结束且结果等于该任务结果

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt1 = " + nextInt);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return nextInt;
        });
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt2 = " + nextInt);
            return nextInt;
        }).whenCompleteAsync((r,e) -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("rrrrrr :" + r);
            } catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        CompletableFuture<Integer> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt3 = " + nextInt);
            return nextInt;
        });

        CompletableFuture<Object> completableFuture = CompletableFuture.anyOf(completableFuture1, completableFuture2,completableFuture3);
        System.out.println("completableFuture.get() = " + completableFuture.get());
    }

    @Test
    public void allOf() throws ExecutionException, InterruptedException {
        //allOf：CompletableFuture.allOf会等待所有传入任务全部执行完成，前提是调用get或者join

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt1 = " + nextInt);
            try {
                TimeUnit.SECONDS.sleep(1);
                //throw new RuntimeException("===");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return nextInt;
        });
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt2 = " + nextInt);
            return nextInt;
        }).whenCompleteAsync((r,e) -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("rrrrrr :" + r);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFuture1, completableFuture2);
        System.out.println("completableFuture.get() = " + completableFuture.get());
    }

    @Test
    public void whenCompleteAsync(){
        //whenCompleteAsync: 上一个任务执行结果和异常作为下一步任务的参数

        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt1 = " + nextInt);
            if (nextInt > 3){
                throw new RuntimeException("big~");
            }
            return nextInt;
        }).whenCompleteAsync((r,e) ->{
            if (e != null){
                System.out.println("error ~ 400");
            }else {
                System.out.println("success ~ r:" + r);
            }
        });


    }

    @Test
    public void thenRunAsync(){
        //thenRunAsync: 上一个任务执行成功则执行当前任务

        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt1 = " + nextInt);
            return nextInt;
        }).thenRunAsync(() ->{
            System.out.println("success ~");
        });


    }


    @Test
    public void thenComposeAsync() throws ExecutionException, InterruptedException {
        //thenComposeAsync : 接收上一个任务的结果作为当前阶段的参数，返回一个completableFuture任务，该任务和执行当前阶段任务（thenComposeAsync）的线程不是一个

        Executor executor = Executors.newFixedThreadPool(3);

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 1. Thread: " + Thread.currentThread().getId() + "Thread name:" + Thread.currentThread().getName());
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt1 = " + nextInt);
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            if (nextInt > 3){
                throw new RuntimeException("nextInt1 is big ~");
            }
            return nextInt;
        },executor);

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 2. Thread: " + Thread.currentThread().getId() + "Thread name:" + Thread.currentThread().getName());
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt2 = " + nextInt);
            return nextInt;
        },executor).thenComposeAsync((r) ->{
            System.out.println("r :" + r +"==="+ Thread.currentThread().getId() + "Thread name:" + Thread.currentThread().getName());
            return completableFuture1;
        },executor).exceptionally(e -> {
            System.out.println("error = " + e);
            return 400;
        });

        System.out.println("completableFuture2.get() = " + completableFuture2.get());

    }

    @Test
    public void thenCombineAsync() throws ExecutionException, InterruptedException {
        //thenCombineAsync : 当前任务和传入任务都执行成功才进入下一个阶段，有返回

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            if (nextInt > 3){
                throw new RuntimeException("nextInt1 is big ~");
            }
            System.out.println("nextInt1 = " + nextInt);
            return nextInt;
        });

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt2 = " + nextInt);
            return nextInt;
        }).thenCombineAsync(completableFuture1, (r1, r2) -> {
            System.out.println("全部执行完成~ r1:" + r1 + " r2:" + r2);
            return r1 + r2;
        }).exceptionally(e -> {
            System.out.println("执行异常" + e.getMessage());
            return null;
        });

        System.out.println("completableFuture2.get() = " + completableFuture2.get());

    }

    @Test
    public void thenAcceptBothAsync() throws ExecutionException, InterruptedException {
        //thenAcceptBothAsync : 当前任务和传入任务都执行成功才进入下一个阶段，无返回

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            if (nextInt > 3){
                throw new RuntimeException("nextInt1 is big ~");
            }
            System.out.println("nextInt1 = " + nextInt);
            return nextInt;
        });

        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt2 = " + nextInt);
            return nextInt;
        }).thenAcceptBothAsync(completableFuture1,(r1,r2) -> {
            System.out.println("全部执行完成~ r1:" + r1 +" r2:"+ r2);
        }).exceptionally(e -> {
            System.out.println("执行异常" + e.getMessage());
            return null;
        });
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }

    }

    @Test
    public void thenApplyAsync() throws ExecutionException, InterruptedException {
        //thenApplyAsync :  当上一阶段执行成功，则将上阶段返回结果当做参数进入下一阶段，如果上一阶段报错则不会执行下一阶段 ,有返回值

        CompletableFuture<Integer> thenApplyAsync = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt = " + nextInt);
            return nextInt;
        }).thenApplyAsync((r) -> {
            System.out.println("上阶段执行成功, 返回结果= " + r);
            return r + 1;
        }).exceptionally((e) -> {
            System.out.println("error:  " + e);
            return 400;
        }).handleAsync((r,e) -> {
            //想要不抛出异常可以加入handleAsync阶段处理
            if (e == null){
                System.out.println("执行成功" );
                return r;
            }else {
                System.out.println("执行失败" );
                return 400;
            }
        });

        System.out.println("thenApplyAsync.get() = " + thenApplyAsync.get());
    }

    @Test
    public void thenAcceptAsync() throws ExecutionException, InterruptedException {
        //thenAcceptAsync : 当上一阶段执行成功，则将上阶段返回结果当做参数进入下一阶段，如果上一阶段报错则不会执行下一阶段，无返回值

        CompletableFuture<Void> thenAcceptAsync = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("nextInt = " + nextInt);
            if(nextInt > 0){
                throw new RuntimeException(" nexInt is big ~");
            }
            return nextInt;
        }).thenAcceptAsync((r) -> {
            System.out.println("上阶段执行成功, 返回结果= " + r);
        }).handleAsync((r,e) -> {
            //想要不抛出异常可以加入handleAsync阶段处理
            if (e == null){
                System.out.println("执行成功" );
            }else {
                System.out.println("执行失败" );
            }
            return r;
        });

        System.out.println("thenAcceptAsync.get() = " + thenAcceptAsync.get());
    }

    @Test
    public void runAfterEitherAsync(){
        //runAfterEitherAsync : 传入一个任务，只要当前任务和传入任务,先执行完成且成功，那么就执行自定义操作，即使执行慢的任务异常也不会影响自定义操作

        CompletableFuture<Integer> aCompletableFuture = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("a nextInt = " + nextInt);
            try {
                //故意延迟200ms，使得当前任务执行慢，且会抛出异常，但是在抛出异常之前b任务已经执行成功，所以不会进入exceptionally异常任务阶段
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nextInt > 0) {
                throw new RuntimeException("a nextInt is big ~");
            }
            return nextInt;
        });
        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("b nextInt = " + nextInt);

            if (nextInt > 15) {
                throw new RuntimeException("b nextInt is big ~");
            }
            return nextInt;
        }).runAfterEitherAsync(aCompletableFuture,() -> {
            System.out.println("a 、b 任务执行成功 ~");
        }).exceptionally((e) -> {
            System.out.println("error :" + e);
            return null;
        });


    }

    @Test
    public void runAfterBothAsync(){
        //runAfterBothAsync : 传入一个任务，只有当前任务和传入任务都执行成功才执行指定步骤，无返回值

        CompletableFuture<Integer> aCompletableFuture = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            if (nextInt > 5) {
                throw new RuntimeException("a nextInt is big ~");
            }
            System.out.println("a nextInt = " + nextInt);
            return nextInt;
        });
        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            if (nextInt > 5) {
                throw new RuntimeException("b nextInt is big ~");
            }
            System.out.println("b nextInt = " + nextInt);
            return nextInt;
        }).runAfterBothAsync(aCompletableFuture,() -> {
            System.out.println("a 、b 任务执行成功 ~");
        }).exceptionally((e) -> {
            System.out.println("error :" + e);
            return null;
        });


    }

    //1

    @Test
    public void handleAsync() throws ExecutionException, InterruptedException {
        //handleAsync : 不管之前的任务是否发生异常，都将上个任务和异常当做参数传入当前任务

        CompletableFuture<Integer> handleAsync = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            if (nextInt > 5) {
                throw new RuntimeException("nextInt is big ~");
            }
            return nextInt;
        }).handleAsync((r,e) -> {
            if (e == null){
                log.error("执行正常，返回结果：{}",r);
                return r;
            }else{
                log.error("发生异常,异常信息：{}",e);
                return 400;
            }
        });

        System.out.println("handleAsync.get() = " + handleAsync.get());
    }

    @Test
    public void exceptionally() throws ExecutionException, InterruptedException {
        //exceptionally : 当之前的任务执行发送异常将当前异常当做参数执行当前步骤

        CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            if (nextInt > 5) {
                throw new RuntimeException("nextInt is big ~");
            }
            return nextInt;
        }).exceptionally(e -> {
            System.out.println("error ;" + e);
            return 400;
        });

        System.out.println("exceptionally.get() = " + exceptionally.get());
    }

    @Test
    public void applyToEitherAsync(){
        //applyToEitherAsync : 传入一个其他的completableFuture任务、和当前任务比较，返回执行较快任务的结果,有返回

        CompletableFuture<String> completableFutureB = CompletableFuture.supplyAsync(() -> {
              return "B" + 10086;
            });
        CompletableFuture.supplyAsync(() -> {
            int nextInt = ThreadLocalRandom.current().nextInt();
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "A" + nextInt;
        }).applyToEitherAsync(completableFutureB,(r) -> {
            return "返回执行较快的结果：" + r;
        }).whenCompleteAsync((r,e) -> {
            System.out.println("r = " + r);
        });
    }

    @Test
    public void acceptEitherAsync(){
        //acceptEitherAsync : 传入一个其他的completableFuture任务、和当前任务比较，返回执行较快任务的结果,无返回

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("this is supplyAsync 有返回值，当前线程：" + Thread.currentThread().getName());
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            return "B" + 10086;
        });

        CompletableFuture.supplyAsync(() -> {
            int i = ThreadLocalRandom.current().nextInt();
            System.out.println(" sleep  有返回值，当前线程：" + Thread.currentThread().getName());
            return "A" + i;
        }).acceptEither(completableFuture1,(r) -> {
            System.out.println("r :" + r);
        });
    }

    @Test
    public void supplyAsync() throws InterruptedException, ExecutionException {

        //supplyAsync : 执行有返回值
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("this is supplyAsync 有返回值，指定线程池，当前线程：" + Thread.currentThread().getName());
            return "supplyAsync 指定线程池";
        }, executorService);

        System.out.println("completableFuture.get() = " + completableFuture.get());

        /*CompletableFuture<Void> exceptionally = CompletableFuture.supplyAsync(() -> {
            System.out.println("this is supplyAsync 有返回值，指定线程池，当前线程：" + Thread.currentThread().getName());
            return "supplyAsync 指定线程池";
        }, executorService).thenAcceptAsync((r) -> {
            System.out.println("thenAccept => 当前线程：" + Thread.currentThread().getName() + " r : " + r);
        }).whenCompleteAsync((r, e) -> {
            System.out.println("whenCompleteAsync => 当前线程：" + Thread.currentThread().getName() + " r : " + r);
            int a = 1 / 0;
        }, executorService).exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });*/
    }

    @Test
    public void runAsync() throws Exception {
        // runAsync : 执行无返回值
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            Thread.currentThread().getName();
            System.out.println("this is runAsync 无返回值，当前线程：" + Thread.currentThread().getName());
        });

        CompletableFuture.runAsync(() -> {
            System.out.println("this is runAsync 无返回，指定线程池，当前线程：" + Thread.currentThread().getName());
        },executorService);

    }

    @Test
    public void completableFutureBaseApi(){

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("current thread name :" + Thread.currentThread().getName());
            //休眠两秒
            try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
            int nextInt = ThreadLocalRandom.current().nextInt(10);
            return nextInt;
        },executorService);

        System.out.println("completableFuture.isCompletedExceptionally() = " + completableFuture.isCompletedExceptionally());

/*        System.out.println("completableFuture.join() = " + completableFuture.join());*/

       /* System.out.println("completableFuture.getNow(400) = " + completableFuture.getNow(400));*/

/*        try {
            System.out.println("completableFuture.get() = " + completableFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

/*        try {
            System.out.println("completableFuture.get(1,TimeUnit.SECONDS) = " + completableFuture.get(1, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/









    }





}
