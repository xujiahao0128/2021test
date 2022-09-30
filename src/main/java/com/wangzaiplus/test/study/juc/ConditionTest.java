package com.wangzaiplus.test.study.juc;

import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.RandomUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import jdk.nashorn.internal.ir.Block;

import java.lang.invoke.MethodHandles;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Mr.Xu
 * @Description Condition条件对象
 * @date 2022年09月28日 15:35
 */
public class ConditionTest {

  public static void main(String[] args) throws InterruptedException {

      ConditionDemo conditionDemo = new ConditionDemo();
      ConditionDemo.Consumer consumer = conditionDemo.new Consumer();
      ConditionDemo.Producer producer = conditionDemo.new Producer();
      producer.start();
      consumer.start();
  }

}

class ConditionDemo{

    private final static int QUEUE_MAX_SIZE = 10;
    private LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue(QUEUE_MAX_SIZE);
    private Lock lock = new ReentrantLock();
    private Condition produceCondition= lock.newCondition();
    private Condition consumeCondition= lock.newCondition();

    class Consumer extends Thread{

        @Override
        public void run() {
            Consume();
        }

        public void Consume(){
            while(true){
                lock.lock();
                try{
                    while (queue.size() == 0){
                        System.out.println("==============当前队列内容为空，等待生产者生产~");
                        consumeCondition.await();
                    }
                    Integer poll = queue.poll();
                    System.out.println(String.format("=================消费者消费产品【%s】",poll));
                    produceCondition.signalAll();
                }catch (Exception e){
                    e.printStackTrace();
                }finally{
                    lock.unlock();
                }
            }
        }

    }

    class Producer extends Thread{

        @Override
        public void run() {
            Produce();
        }

        public void Produce(){
            while(true){
                lock.lock();
                try{
                    while (queue.size() == QUEUE_MAX_SIZE){
                        System.out.println("++++++++++++++++++当前队列内容已满，等待消费者消费~");
                        produceCondition.await();
                    }
                    int nextInt = RandomUtil.getRandom().nextInt();
                    if (queue.offer(nextInt)) {
                        System.out.println(String.format("++++++++++++++++++生产者新增产品【%s】",nextInt));
                        consumeCondition.signalAll();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally{
                    lock.unlock();
                }
            }
        }

    }

}



