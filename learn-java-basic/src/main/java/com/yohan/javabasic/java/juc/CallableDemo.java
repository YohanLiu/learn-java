package com.yohan.javabasic.java.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * futureTask如果想在循环内多次执行，需要每次循环都新new一个futureTask在执行才可.
 * <p>具体对比可看{@link #testCallableLoopWithNew()}}和{@link #testCallableLoopWithNoNew()}.
 * @author yohan
 * @Date 2024/01/20
 */
public class CallableDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> task = new FutureTask<>(new CallableThead());
        new Thread(task).start();

        FutureTask<String> task1 = new FutureTask<>(() -> {
            System.out.println("Callable的Lambda表达式的线程" + Thread.currentThread().getName());
            return "Callable的Lambda表达式的线程返回值";
        });
        new Thread(task1, "名字叫名字").start();

        System.out.println("CallableDemo 线程返回值：" + task.get());
        System.out.println("CallableDemo lambda 线程返回值：" + task1.get());

        System.out.println("测试循环分割线-------------------------------------------------------------------------");
        testCallableLoopWithNoNew();
        testCallableLoopWithNew();
    }

    private static void testCallableLoopWithNoNew() throws ExecutionException, InterruptedException {
        // 输出结果：
//        线程内部输出语句CallableLoopWithNoNew循环体内没新建futureTask的线程0
//        CallableLoopWithNoNew的for循环输出get获取线程返回值CallableLoopWithNoNew循环体内没新建futureTask的线程0
//        CallableLoopWithNoNew的for循环输出get获取线程返回值CallableLoopWithNoNew循环体内没新建futureTask的线程0
//        CallableLoopWithNoNew的for循环输出get获取线程返回值CallableLoopWithNoNew循环体内没新建futureTask的线程0
        FutureTask<String> futureTask = new FutureTask<>(new CallableThreadTestLoop());
        for (int j = 0; j < 3; j++) {
            Thread threadLoopWithNoNew = new Thread(futureTask, "CallableLoopWithNoNew循环体内没新建futureTask的线程" + j);
            threadLoopWithNoNew.start();
            System.out.println("CallableLoopWithNoNew的for循环输出get获取线程返回值" + futureTask.get());
        }
    }

    private static void testCallableLoopWithNew() throws ExecutionException, InterruptedException {
        // 输出结果：
//        线程内部输出语句循环体内没新建futureTask的线程0
//        for循环输出get获取线程返回值循环体内没新建futureTask的线程0
//        线程内部输出语句循环体内没新建futureTask的线程1
//        for循环输出get获取线程返回值循环体内没新建futureTask的线程1
//        线程内部输出语句循环体内没新建futureTask的线程2
//        for循环输出get获取线程返回值循环体内没新建futureTask的线程2
//        线程内部输出语句循环体内没新建futureTask的线程3
//        for循环输出get获取线程返回值循环体内没新建futureTask的线程3
//        线程内部输出语句循环体内没新建futureTask的线程4
//        for循环输出get获取线程返回值循环体内没新建futureTask的线程4
        for (int j = 0; j < 5; j++) {
            FutureTask<String> futureTask = new FutureTask<>(new CallableThreadTestLoop());
            Thread threadLoopWithNew = new Thread(futureTask, "循环体内没新建futureTask的线程" + j);
            threadLoopWithNew.start();
            System.out.println("for循环输出get获取线程返回值" + futureTask.get());
        }
    }
}


class CallableThreadTestLoop implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("线程内部输出语句" + Thread.currentThread().getName());
        return Thread.currentThread().getName();
    }
}

class CallableThead implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        int i = 0;
        for (; i < 5; i++) {
            System.out.println("实现Callable接口的线程");
        }
        return i;
    }
}