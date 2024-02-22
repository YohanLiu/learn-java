package com.yohan.javabasic.java.thread;

public class AlternatePrintDigit {
    public static final Object obj = new Object();
    public static int number = 1;
    public static final int MAX_NUMBER = 100;

    public static void main(String[] args) {
        PrintDigit printDigit = new PrintDigit();
        Thread thread1 = new Thread(printDigit, "线程1");
        Thread thread2 = new Thread(printDigit, "线程2");

        thread1.start();
        thread2.start();
    }


    static class PrintDigit implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (obj) {
                    obj.notify();
                    if (number <= MAX_NUMBER) {
                        System.out.println(Thread.currentThread().getName() + ":" + number++);
                    } else {
                        break;
                    }

                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    // 另一种写法
    static class PrintDigit1 implements Runnable {
        @Override
        public void run() {
            while (number <= MAX_NUMBER) {
                synchronized (obj) {
                    obj.notify();

                    System.out.println(Thread.currentThread().getName() + ":" + number++);
                    if (number <= MAX_NUMBER) {
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }
}


