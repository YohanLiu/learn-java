package com.yohan.javabasic.java.juc;

import java.util.Random;
import java.util.concurrent.*;

/**
 * <p>ThreadLocal一定要初始化，避免空指针异常.
 * <p>建议把ThreadLocal修饰为static.
 * <p>
 * <p>ThreadLocal底层的ThreadLocalMap的key是当前ThreadLocalMap的对象，对应如下代码的saleVolume.
 * <p>ThreadLocalMap的底层是Entry，key最终是个弱引用.
 * <p>debug一下就可以看得出来.
 */
class House {
    int saleCount = 0;

    public synchronized void saleHouse() {
        ++saleCount;
    }

    /*ThreadLocal<Integer> saleVolume = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue()
        {
            return 0;
        }
    };*/
    static ThreadLocal<Integer> saleVolume = ThreadLocal.withInitial(() -> 0);

    public void saleVolumeByThreadLocal() {
        saleVolume.set(1 + saleVolume.get());
    }
}

/**
 * 销售卖完随机数房子，各自独立销售额度，自己业绩按提成走，分灶吃饭.
 *
 * <p>必须回收自定义的 ThreadLocal 变量，尤其在线程池场景下，线程经常会被复用，如果不清理
 * 自定义的 ThreadLocal 变量，可能会影响后续业务逻辑和造成内存泄露等问题。尽量在代理中使用
 * try-finally 块进行回收.
 */
public class ThreadLocalDemo {
    public static void main(String[] args) throws InterruptedException {
        House house = new House();

        ExecutorService threadPool = new ThreadPoolExecutor(
                5,
                8,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            for (int i = 0; i < 5; i++) {
                threadPool.submit(() -> {
                    int size = new Random().nextInt(5) + 1;
                    try {
                        for (int j = 1; j <= size; j++) {
                            house.saleHouse();
                            house.saleVolumeByThreadLocal();
                        }
                        System.out.println(Thread.currentThread().getName() + "\t" + "号销售卖出：" + house.saleVolume.get());
                    } finally {
                        house.saleVolume.remove();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }

        //暂停毫秒
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "\t" + "共计卖出多少套： " + house.saleCount);
    }
}