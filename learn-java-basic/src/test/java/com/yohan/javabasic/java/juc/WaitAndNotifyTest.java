package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author yohan
 * @Date 2024/03/11
 */
public class WaitAndNotifyTest {
    /**
     * 线程需要先获得并持有锁，必须在锁块（synchronized或lock）中.
     */
    @Test
    public void test01() {
        Object objectLock = new Object();

        new Thread(() -> {
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                try {
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");
            }
        }, "t1").start();

        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            synchronized (objectLock) {
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t ----发出通知");
            }
        }, "t2").start();


        // 单元测试是不支持多线程的，因为当主线程结束以后，无论子线程结束与否，都会强制退出程序，主线程优先级最高
        // 所以为了看到效果，延长主线程存活时间
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 线程需要先获得并持有锁，必须在锁块（synchronized或lock）中.
     * <p>如下代码没在锁块中，就会报错.
     */
    @Test
    public void test02() {
        Object objectLock = new Object();

        new Thread(() -> {

            System.out.println(Thread.currentThread().getName() + "\t ----come in");
            try {
                objectLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");

        }, "t1").start();

        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {

            objectLock.notify();
            System.out.println(Thread.currentThread().getName() + "\t ----发出通知");

        }, "t2").start();


        // 单元测试是不支持多线程的，因为当主线程结束以后，无论子线程结束与否，都会强制退出程序，主线程优先级最高
        // 所以为了看到效果，延长主线程存活时间
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 必须要先等待后唤醒，线程才能够被唤醒.
     * <p>如下代码线程就会被阻塞.
     */
    public static void main(String[] args) {
        Object objectLock = new Object();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                try {
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (objectLock) {
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t ----发出通知");
            }
        }, "t2").start();
    }

}
