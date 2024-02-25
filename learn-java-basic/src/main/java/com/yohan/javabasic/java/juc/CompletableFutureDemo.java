package com.yohan.javabasic.java.juc;

import java.util.concurrent.*;

/**
 * 测试completableFuture使用自定义线程池.
 * <p>自定义线程池是非守护线程，不会出现使用completableFuture默认线程池(守护线程)导致的主线程结束
 * <p>completableFuture中任务结果不输出的现象.
 */
public class CompletableFutureDemo {
    public static void main(String[] args) {
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,
                5,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 异步调用
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " : CompletableFuture");
            System.out.println("threadPool自定义线程池是守护线程吗：" + Thread.currentThread().isDaemon());
            return 1024;
        }, threadPool);
        System.out.println("主线程继续执行");

        // 不关闭线程池的话主线程不会结束
        threadPool.shutdown();
    }
}