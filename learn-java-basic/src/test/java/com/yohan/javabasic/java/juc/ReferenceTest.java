package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.lang.ref.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yohan
 * @Date 2024/03/18
 */
public class ReferenceTest {
    class MyObject {
        // 这个方法一般不用复写，只是为了学习用
        @Override
        protected void finalize() throws Throwable {
            // finalize的通常目的是在对象被不可撤销地丢弃之前执行清理操作。
            System.out.println("-------invoke finalize method~!!!");
        }
    }

    /**
     * 对于强引用的对象，就算是出现了OOM也不会对该对象进行回收.它处于可达状态，是不可能被垃圾回收机制回收的.
     * <p>除非对象引用置为null，或者超过作用域，即变为不可达，此时可以回收.
     */
    @Test
    public void strongReferenceTest() {
        MyObject myObject = new MyObject();
        System.out.println("gc before: " + myObject);

        myObject = null;
        // 此方法只是建议进行GC，不一定肯定会发生GC
        System.gc();//人工开启GC，一般不用

        //暂停毫秒
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("gc after: " + myObject);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 运行此测试方法前需设置idea的VM options: -Xms10m -Xmx10m.
     * <p>软引用当系统内存充足时，不会被回收，当系统内存不足时，他会被回收.
     */
    @Test
    public void softReferenceTest() {
        SoftReference<MyObject> softReference = new SoftReference<>(new MyObject());
        System.out.println("-----softReference:" + softReference.get());

        // 此方法只是建议进行GC，不一定肯定会发生GC
        System.gc();//人工开启GC，一般不用
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----gc after内存够用: " + softReference.get());

        try {
            byte[] bytes = new byte[20 * 1024 * 1024];//20MB对象
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("-----gc after内存不够: " + softReference.get());
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对于只有弱引用的对象而言，只要垃圾回收机制一运行，不管JVM的内存空间是否足够，都会回收该对象占用的内存.
     */
    @Test
    public void weakReferenceTest() {
        WeakReference<MyObject> weakReference = new WeakReference<>(new MyObject());
        System.out.println("-----gc before 内存够用： " + weakReference.get());

        // 此方法只是建议进行GC，不一定肯定会发生GC
        System.gc();//人工开启GC，一般不用
        //暂停几秒钟线程
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("-----gc after 内存够用： " + weakReference.get());
    }

    /**
     * 运行此测试方法前需设置idea的VM options: -Xms30m -Xmx30m.
     * <p>虚引用必须和引用队列联合使用.
     * <p>如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都有可能被垃圾回收器回收，它不能单独使用也不能通过它访问对象.
     */
    @Test
    public void phantomReferenceTest() {
        ReferenceQueue<MyObject> referenceQueue = new ReferenceQueue<>();
        PhantomReference<MyObject> phantomReference = new PhantomReference<>(new MyObject(), referenceQueue);
        System.out.println("-----phantomReference初始:" + phantomReference.get());

        List<byte[]> list = new ArrayList<>();

        CountDownLatch countDownLatch = new CountDownLatch(2);

        new Thread(() -> {
            while (true) {
                list.add(new byte[1 * 1024 * 1024]);
                try {
                    TimeUnit.MILLISECONDS.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                System.out.println(phantomReference.get() + "\t" + "list add ok");
            }
            countDownLatch.countDown();

        }, "t1").start();

        new Thread(() -> {
            while (true) {
                Reference<? extends MyObject> reference = referenceQueue.poll();
                if (reference != null) {
                    System.out.println("-----有虚对象回收加入了队列");
                    break;
                }
            }
            countDownLatch.countDown();
        }, "t2").start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
