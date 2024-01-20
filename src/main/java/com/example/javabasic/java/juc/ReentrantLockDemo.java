package com.example.javabasic.java.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 第一步  创建资源类，定义属性和和操作方法
class LTicket {
    // 票数量
    private int number = 30;

    // 创建可重入锁公平锁
    private final ReentrantLock lock = new ReentrantLock(true);

    // 卖票方法
    public void sale() {
        //上锁
        lock.lock();
        try {
            // 判断是否有票
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + " ：卖出" + (number--) + " 剩余：" + number);
            }
        } finally {
            // 解锁
            lock.unlock();
        }
    }
}

public class ReentrantLockDemo {
    private static LTicket ticket = new LTicket();

    // 第二步 创建多个线程，调用资源类的操作方法
    public static void main(String[] args) {
        // 创建三个线程
        new Thread(new LSellTicket(), "AA").start();

        new Thread(new LSellTicket(), "BB").start();

        new Thread(new LSellTicket(), "CC").start();

        testReentrantLock();
    }

    static class LSellTicket implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 35; i++) {
                ticket.sale();
            }
        }
    }

    private static void testReentrantLock() {
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
        }, "t1").start();
    }
}