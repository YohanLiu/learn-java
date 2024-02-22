package com.yohan.javabasic.java.juc;

/**
 * @author yohan
 * @Date 2024/01/20
 */
public class RunnableDemo {
    public static void main(String[] args) {
        new Thread(new runnableThread()).start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Runnable的Lambda表达式的线程");
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("我的名字叫" + Thread.currentThread().getName());
            }
        }, "我是名字").start();
    }
}

class runnableThread implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("实现Runnable接口的线程");
        }
    }
}