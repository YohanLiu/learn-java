package com.yohan.javabasic.java.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableTest {
    public static void main(String[] args) {
        PrintNumByCallable printNumByCallable = new PrintNumByCallable();
        FutureTask<Integer> integerFutureTask = new FutureTask<>(printNumByCallable);
        Thread thread = new Thread(integerFutureTask);
        thread.start();

        try {
            Integer sum = integerFutureTask.get();
            System.out.println(Thread.currentThread().getName() + ": sum is " + sum);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}


class PrintNumByCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 0; i <= 100; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
            sum += i;
        }
        return sum;
    }
}