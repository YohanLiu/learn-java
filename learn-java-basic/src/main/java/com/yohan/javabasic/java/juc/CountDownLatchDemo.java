package com.yohan.javabasic.java.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

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

        System.out.println(Thread.currentThread().getName() + " 线程继续执行");
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

