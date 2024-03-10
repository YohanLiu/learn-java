package com.yohan.javabasic.java.juc.Interrupt;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author yohan
 * @Date 2024/03/10
 */
public class InterruptApiTest {
    @Test
    public void testInterrupt() {
        //实例方法interrupt()仅仅是设置线程的中断状态位设置为true，不会停止线程
        Thread t1 = new Thread(() -> {
            for (int i = 1; i <= 300; i++) {
                System.out.println("-----: " + i);
            }
            System.out.println("t1线程调用interrupt()后的的中断标识02：" + Thread.currentThread().isInterrupted());
        }, "t1");
        t1.start();

        System.out.println("t1线程默认的中断标识：" + t1.isInterrupted());//false

        //暂停毫秒
        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();//true
        System.out.println("t1线程调用interrupt()后的的中断标识01：" + t1.isInterrupted());//true

        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // jdk 17测试输出true，jdk17是直接返回interrupted这个类变量
        // jdk 8测试输出false，因为jdk8中断不活动的线程不会产生任何影响
        // 究其原因可以debug看到，具体实现有差异
        System.out.println("t1线程调用interrupt()后的的中断标识03：" + t1.isInterrupted());
    }

    /**
     * 1 中断标志位，默认false
     * <p>2 t2 ----> t1发出了中断协商，t2调用t1.interrupt()，中断标志位true
     * <p>3 中断标志位true，正常情况，程序停止，^_^
     * <p>4 中断标志位true，异常情况，InterruptedException，将会把中断状态将被清除，并且将收到InterruptedException 。中断标志位false
     * 导致无限循环
     * <p>5 在catch块中，需要再次给中断标志位设置为true，2次调用停止程序才OK
     */
    @Test
    public void testBlockedByInterrupt() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "\t " +
                            "中断标志位：" + Thread.currentThread().isInterrupted() + " 程序停止");
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();//为什么要在异常处，再调用一次？？
                    e.printStackTrace();
                }

                System.out.println("-----hello InterruptDemo3");
            }
        }, "t1");
        t1.start();

        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> t1.interrupt(), "t2").start();
    }

    @Test
    public void testStaticMethodInterrupted() {
        // 测试当前线程是否被中断（检查中断标志），返回一个boolean并清除中断状态，
        // 第二次再调用时中断状态已经被清除，将返回一个false。
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted());
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted());
        System.out.println("----1");
        Thread.currentThread().interrupt();// 中断标志位设置为true
        System.out.println("----2");
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted());
        System.out.println(Thread.currentThread().getName() + "\t" + Thread.interrupted());

        LockSupport.park();

        Thread.interrupted();//静态方法

        Thread.currentThread().isInterrupted();//实例方法
    }
}
