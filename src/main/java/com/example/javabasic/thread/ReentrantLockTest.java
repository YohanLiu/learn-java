package com.example.javabasic.thread;

import java.util.concurrent.locks.ReentrantLock;

class Window extends Thread {
    public static int ticket = 100;
    // 创建Lock的实例，需要确保多个线程共用同一个Lock实例!需要考虑将此对象声明为static final
    private static final ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while (true) {
            try {
                // 执行lock()方法，锁定对共享资源的调用
                lock.lock();
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + "售票，票号为:" + ticket);
                    ticket--;
                } else {
                    break;
                }
            } finally {
                // unlock()的调用，释放对共享数据的锁定
                lock.unlock();
            }
        }
    }
}

public class ReentrantLockTest {
    public static void main(String[] args) {
        Window w1 = new Window();
        Window w2 = new Window();
        Window w3 = new Window();

        w1.setName("窗口1");
        w2.setName("窗口2");
        w3.setName("窗口3");

        w1.start();
        w2.start();
        w3.start();
    }
}
