package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yohan
 * @Date 2024/03/10
 */
public class ReEntryLockTest {

    @Test
    public void reEntryByLock() {
        // Lock演示可重入锁
        Lock lock = new ReentrantLock();
        // 创建线程
        new Thread(() -> {
            try {
                // 上锁
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " 外层");

                try {
                    // 上锁
                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + " 内层");
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            } finally {
                // 释放做
                lock.unlock();
            }
        }, "ReentrantLock可重入锁").start();
    }

    @Test
    public void reEntryBySynchronized() {
        final Object object = new Object();
        new Thread(() -> {
            synchronized (object) {
                System.out.println(Thread.currentThread().getName() + "\t ----外层调用");
                synchronized (object) {
                    System.out.println(Thread.currentThread().getName() + "\t ----中层调用");
                    synchronized (object) {
                        System.out.println(Thread.currentThread().getName() + "\t ----内层调用");
                    }
                }
            }
        }, "synchronized可重入锁").start();
    }
}
