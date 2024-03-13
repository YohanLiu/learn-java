package com.yohan.javabasic.java.juc;

import java.util.concurrent.TimeUnit;

class MyNumber {
    volatile int number;

    volatile int number2;

    public void addNumber() {
        number++;
    }

    /**
     * volatile关键字不具有原子性.
     * <p>原子性要通过加锁来保证.
     */
    public synchronized void addNumber2() {
        number2++;
    }

}

/**
 * 演示volatile关键字不具有原子性.
 *
 * @auther yohan
 */
public class VolatileNoAtomicDemo {
    public static void main(String[] args) {
        MyNumber myNumber = new MyNumber();

        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                for (int j = 1; j <= 1000; j++) {
                    myNumber.addNumber();
                    myNumber.addNumber2();
                }
            }, String.valueOf(i)).start();
        }

        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("volatile修饰变量，方法不加synchronized：" + myNumber.number);
        System.out.println("-----------------------------------------------------");
        System.out.println("volatile修饰变量，方法添加synchronized：" + myNumber.number2);
    }
}