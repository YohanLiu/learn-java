package com.yohan.javabasic.java.juc;

// 第一步  创建资源类，定义属性和和操作方法
class Ticket {
    // 票数
    private int number = 100;

    // 操作方法：卖票
    public synchronized void sale() {
        //判断：是否有票
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + " : 卖出：" + (number--) + " 剩下：" + number);
        }
    }
}

public class SynchronizedDemo {
    // 创建Ticket对象
    private static Ticket ticket = new Ticket();

    // 第二步 创建多个线程，调用资源类的操作方法
    public static void main(String[] args) {

        // 创建三个线程
        new Thread(new SellTicket(), "AA").start();

        new Thread(new SellTicket(), "BB").start();

        new Thread(new SellTicket(), "CC").start();
    }

    static class SellTicket implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 35; i++) {
                ticket.sale();
            }
        }
    }
}

