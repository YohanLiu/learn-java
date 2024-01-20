package com.example.javabasic.java.juc;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    // 创建固定值
    private static final int NUMBER = 7;

    public static void main(String[] args) {
        // 创建CyclicBarrier
        // 下方会执行await将number的值减少，等于0则会执行Runnable这定义的代码
        CyclicBarrier cyclicBarrier =
                new CyclicBarrier(NUMBER, () -> System.out.println("*****集齐7颗龙珠就可以召唤神龙"));

        // 集齐七颗龙珠过程
        for (int i = 1; i <= 7; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 星龙被收集到了");
                    // 会将number - 1
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}