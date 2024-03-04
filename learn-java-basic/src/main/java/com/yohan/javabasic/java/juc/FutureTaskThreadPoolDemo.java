package com.yohan.javabasic.java.juc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FutureTaskThreadPoolDemo
{
    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        // 3个任务，目前开启多个异步任务线程来处理，耗时504ms
        // 如果同样的3个任务，同步执行，那耗时是1101ms

        ExecutorService threadPool = new ThreadPoolExecutor(
                3,
                5,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        long startTime = System.currentTimeMillis();

        FutureTask<String> futureTask1 = new FutureTask<String>(() -> {
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            return "task1 over";
        });
        threadPool.submit(futureTask1);

        FutureTask<String> futureTask2 = new FutureTask<String>(() -> {
            try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            return "task2 over";
        });
        threadPool.submit(futureTask2);

        FutureTask<String> futureTask3 = new FutureTask<String>(() -> {
            try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            return "task2 over";
        });
        threadPool.submit(futureTask3);

        // get 会阻塞当前线程，直到获取到结果
        System.out.println(futureTask1.get());
        System.out.println(futureTask2.get());
        System.out.println(futureTask3.get());

        long endTime = System.currentTimeMillis();
        System.out.println("----异步执行costTime: "+(endTime - startTime) +" 毫秒");


        System.out.println(Thread.currentThread().getName()+"\t -----end");
        threadPool.shutdown();

        synchronization();
    }

    public static void synchronization() {
        // 3个任务，目前只有一个线程main来处理，耗时1102ms

        long startTime = System.currentTimeMillis();
        //暂停毫秒
        try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
        try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

        long endTime = System.currentTimeMillis();
        System.out.println("----synchronization同步执行costTime: "+(endTime - startTime) +" 毫秒");

        System.out.println(Thread.currentThread().getName()+"\t -----end");
    }
}