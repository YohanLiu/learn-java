package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yohan
 * @Date 2024/03/11
 */
public class AwaitAndSignalTest {
    /**
     * 线程需要先获得并持有锁，必须在锁块（synchronized或lock）中.
     */
    @Test
    public void test01() {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        // 暂停几秒钟线程
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                //condition.signalAll();
                System.out.println(Thread.currentThread().getName() + "\t ----发出通知");
            } finally {
                lock.unlock();
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
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {

            condition.signal();
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
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t ----come in");
                condition.await();
                System.out.println(Thread.currentThread().getName() + "\t ----被唤醒");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                condition.signal();
                System.out.println(Thread.currentThread().getName() + "\t ----发出通知");
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }
}
