package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author yohan
 * @Date 2024/03/16
 */
public class CASTest {

    @Test
    public void CASTest() {
        AtomicInteger atomicInteger = new AtomicInteger(5);

        System.out.println(atomicInteger.compareAndSet(5, 2024) + "\t" + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 2024) + "\t" + atomicInteger.get());

        atomicInteger.getAndIncrement();
    }

    @Test
    public void CASHappenABATest() {
        AtomicInteger atomicInteger = new AtomicInteger(100);
        new Thread(() -> {
            atomicInteger.compareAndSet(100, 101);
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicInteger.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicInteger.compareAndSet(100, 2024) + "\t" + atomicInteger.get());
        }, "t2").start();

        // 单元测试是不支持多线程的，因为当主线程结束以后，无论子线程结束与否，都会强制退出程序，主线程优先级最高
        // 所以为了看到效果，延长主线程存活时间
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ABATest() {
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(100, 1);
        new Thread(() -> {
            int stamp = stampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t" + "首次版本号：" + stamp);

            //暂停500毫秒,保证后面的t4线程初始化拿到的版本号和我一样
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stampedReference.compareAndSet(100, 101, stampedReference.getStamp(), stampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t" + "2次流水号：" + stampedReference.getStamp());

            stampedReference.compareAndSet(101, 100, stampedReference.getStamp(), stampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t" + "3次流水号：" + stampedReference.getStamp());

        }, "t3").start();

        new Thread(() -> {
            int stamp = stampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t" + "首次版本号：" + stamp);

            //暂停1秒钟线程,等待上面的t3线程，发生了ABA问题
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean b = stampedReference.compareAndSet(100, 2022, stamp, stamp + 1);

            System.out.println(Thread.currentThread().getName() + "\t" + b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());

        }, "t4").start();

        // 单元测试是不支持多线程的，因为当主线程结束以后，无论子线程结束与否，都会强制退出程序，主线程优先级最高
        // 所以为了看到效果，延长主线程存活时间
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
