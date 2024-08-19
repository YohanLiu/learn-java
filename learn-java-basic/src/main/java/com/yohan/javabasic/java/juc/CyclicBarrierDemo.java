package com.yohan.javabasic.java.juc;

import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier 会自动进行重置.
 * 如果我们调用 reset 方法，但还有线程在等待，就会导致等待线程被打扰，抛出 BrokenBarrierException 异常。
 * CyclicBarrier 侧重点是线程，而不是调用事件，它的典型应用场景是用来等待并发线程结束。
 */
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
                    for (int j = 1; j <= 2; j++) {
                        System.out.println(Thread.currentThread().getName() + " 星龙被收集到了");
                        // 会将number - 1
                        cyclicBarrier.await();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}