package com.example.javabasic.java.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(10, 10,
                60L, TimeUnit.SECONDS, new ArrayBlockingQueue(10));

        executor.submit(new PrintEvenByCallable());
        executor.submit(new PrintOddByCallable());
        executor.shutdown();

        ExecutorService executor1 = new ThreadPoolExecutor(10, 10,
                60L, TimeUnit.SECONDS, new ArrayBlockingQueue(10));
        executor1.submit(new PrintMyNameByCallable());
        executor1.shutdown();

    }
}

class PrintMyNameByCallable implements Callable {
    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 10; i++) {
            System.out.println(Thread.currentThread().getName() + ": my name is yohan");
        }
        return null;
    }
}

class PrintOddByCallable implements Callable {
    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
        return null;
    }
}

class PrintEvenByCallable implements Callable {
    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ": " + i);
            }
        }
        return null;
    }
}
