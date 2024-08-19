package com.yohan.javabasic.java.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * CountDownLatch 是不可以重置的，所以无法重用.
 * 不管你是在一个线程还是多个线程里 countDown，只要次数足够即可.
 * CountDownLatch 操作的是事件。
 */
public class CountDownLatchDemo {
    // 创建CountDownLatch对象，设置初始值
    private static CountDownLatch countDownLatch = new CountDownLatch(10);

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println("Runnable" + Thread.currentThread().getName() + " 号执行完毕");

                // 计数  -1
                countDownLatch.countDown();

            }, String.valueOf(i)).start();

            new Thread(new FutureTask<>(new ThreadCallable()), String.valueOf(i)).start();
        }

        // 等待
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + " 线程继续执行-" + countDownLatch.getCount());

        // 无法重用
        new Thread(() -> {
            for (int i = 1; i < 5; i++) {
                // 计数  -1
                countDownLatch.countDown();
            }

            System.out.println(Thread.currentThread().getName() + " 执行完毕-" + countDownLatch.getCount());


        }, "countdown 0").start();

        // 一个线程也可以,只要次数够就行
        CountDownLatch countDownLatch1 = new CountDownLatch(6);
        new Thread(() -> {
            for (int i = 1; i < 10; i++) {
                // 计数  -1
                countDownLatch1.countDown();
            }

            System.out.println(Thread.currentThread().getName() + " 执行完毕-" + countDownLatch1.getCount());


        }, "countDownLatch1").start();
    }


    static class ThreadCallable implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("Callable" + Thread.currentThread().getName() + " 号执行完毕");
            // 计数  -1
            countDownLatch.countDown();
            return "Callable" + Thread.currentThread().getName() + " 号执行完毕";
        }
    }
}

