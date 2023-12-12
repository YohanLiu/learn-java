package com.example.javabasic.thread;

import static com.example.javabasic.thread.PrintNumberTest.loop;

public class PrintNumberTest {
    public static int loop = 100;

    public static void main(String[] args) {
        // 打印偶数的线程
        PrintEvenByThread printEvenByThread = new PrintEvenByThread();
        printEvenByThread.start();

        // main线程打印
        for (int i = 0; i <= loop; i++) {
            System.out.println(Thread.currentThread().getName() + ":" + i);
        }

        // 打印奇数的线程
        PrintOddByRunnalbe printOddByRunnalbe = new PrintOddByRunnalbe();
        Thread thread = new Thread(printOddByRunnalbe);
        thread.start();

    }
}

// 继承Thread来实现线程
class PrintEvenByThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i <= loop; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
}

// 实现Runnable来实现线程
class PrintOddByRunnalbe implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i <= loop; i++) {
            if (i % 2 != 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
}