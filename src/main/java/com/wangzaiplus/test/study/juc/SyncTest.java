package com.wangzaiplus.test.study.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2022年06月21日 15:39
 */
public class SyncTest {

    /** 1.标准的两个线程先后访问同一个对象，是先发邮件还是先打游戏？
     *  2.sendEmail方法线程暂停3秒，是先发邮件还是先打游戏？
     *  3.调用非同步方法hello方法和发邮件，是先发邮件还是先hello？
     *  4.有两个MySource对象，分别调用发邮件和打游戏，请问是先发邮件还是先打游戏？
     *  5.有两个静态同步方法一个MyResource对象，是先发邮件还是先打游戏？
     *  6.有两个静态同步方法两个MyResource对象，是先发邮件还是先打游戏？
     *  7.有一个静态同步方法，一个普通同步方法，一个MyResource对象，是先发邮件还是先打游戏？
     *  8.有一个静态同步方法，一个普通同步方法，两个MyResource对象，是先发邮件还是先打游戏？*/
    @Test
    public void test1() throws InterruptedException {
        MyResource myResource = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource.sendEmail();
        });
        t1.start();

        //主线程睡一秒
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource.playGame();
        });
        t2.start();
    }

    @Test
    public void test2() throws InterruptedException {
        //sendEmail方法线程暂停3秒，是先发邮件还是先打游戏？
        MyResource myResource = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource.sendEmail();
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            myResource.playGame();
        });
        t2.start();

        //主线程睡5秒,主线程等待守护线程执行完成，否则打印不出来
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test3() throws InterruptedException {
        //调用非同步方法hello方法和发邮件，是先发邮件还是先hello？
        MyResource myResource = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource.sendEmail();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource.helloWord();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test4() throws InterruptedException {
        //有两个MySource对象，分别调用发邮件和打游戏，请问是先发邮件还是先打游戏？
        MyResource myResource1 = new MyResource();
        MyResource myResource2 = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource1.sendEmail();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource2.playGame();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test5() throws InterruptedException {
        //有两个静态同步方法一个MyResource对象，是先发邮件还是先打游戏？
        MyResource myResource = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource.sendEmailStatic();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource.playGameStatic();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test6() throws InterruptedException {
        //有两个静态同步方法两个MyResource对象，是先发邮件还是先打游戏？
        MyResource myResource1 = new MyResource();
        MyResource myResource2 = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource1.sendEmailStatic();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource2.playGameStatic();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test7() throws InterruptedException {
        //有一个静态同步方法，一个普通同步方法，一个MyResource对象，是先发邮件还是先打游戏？
        MyResource myResource1 = new MyResource();
        MyResource myResource2 = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource1.sendEmailStatic();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource1.playGame();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

    @Test
    public void test8() throws InterruptedException {
        //有一个静态同步方法，一个普通同步方法，两个MyResource对象，是先发邮件还是先打游戏？
        MyResource myResource1 = new MyResource();
        MyResource myResource2 = new MyResource();

        Thread t1 = new Thread(() -> {
            myResource1.sendEmailStatic();
        });
        t1.start();

        //主线程睡1s
        TimeUnit.SECONDS.sleep(1);

        Thread t2 = new Thread(() -> {
            myResource2.playGame();
        });
        t2.start();

        //主线程睡5s,等待守护线程执行完成
        TimeUnit.SECONDS.sleep(5);
    }

}

class MyResource{

    public synchronized void sendEmail(){
        //当前线程睡3秒
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=======普通同步sendEmail");
    }

    public synchronized void playGame(){
        System.out.println("=======普通同步playGame");
    }

    public static synchronized void sendEmailStatic(){
        //当前线程睡3秒
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=======静态同步sendEmail");
    }

    public static synchronized void playGameStatic(){
        System.out.println("=======静态普通同步playGame");
    }

    public void helloWord(){
        System.out.println("=======普通方法helloWord");
    }

}
