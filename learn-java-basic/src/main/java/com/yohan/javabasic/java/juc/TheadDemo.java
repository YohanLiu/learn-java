package com.yohan.javabasic.java.juc;

/**
 * @author yohan
 * @Date 2024/01/20
 */
public class TheadDemo {
    public static void main(String[] args) {
        new thread1().start();
    }
}

/**
 * 继承Thread来实现线程.
 */
class thread1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("继承Thread类实现线程");
        }
    }
}