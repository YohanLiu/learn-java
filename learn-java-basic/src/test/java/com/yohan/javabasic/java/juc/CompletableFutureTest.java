package com.yohan.javabasic.java.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

/**
 * @author yohan
 * @Date 2024/02/25
 */
public class CompletableFutureTest {
    /**
     * 可以看到CompletableFuture默认调用的线程池是守护线程.
     * <p>所以当主线程（非守护线程）结束时, 只剩下守护线程，
     * <p>守护线程也会结束.所以没输出.
     */
    @Test
    public void test01() {
        //异步调用
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " : CompletableFutureTest01");
            return 1024;
        });

        System.out.println("主线程继续执行");
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 用来证明CompletableFuture默认的线程池ForkJoinPool是守护线程.
     */
    @Test
    public void test02() {
        //异步调用
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : CompletableFutureTest02");
            System.out.println("CompletableFuture默认线程是守护线程吗：" + Thread.currentThread().isDaemon());
            return 1024;
        });
        System.out.println("主线程是守护线程吗：" + Thread.currentThread().isDaemon());
        System.out.println("主线程继续执行");
    }

    /**
     * JUnit@Test的测试 不支持多线程，代码执行完就会直接退出，不会检测子线程是否结束.
     * 所以测试completableFuture使用自定义线程池请看 {@link CompletableFutureDemo}.
     */
    @Test
    public void test03() {
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,
                5,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 异步调用
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " : CompletableFutureTest03");
            System.out.println("threadPool是守护线程吗：" + Thread.currentThread().isDaemon());
            return 1024;
        }, threadPool);
        System.out.println("主线程继续执行");
        threadPool.shutdown();
    }

    @Test
    public void test04() {
        //同步调用
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : CompletableFuture1");
        });
        try {
            System.out.println(completableFuture1.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test05() {
        // mq消息队列
        // 异步调用
        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : CompletableFuture2");
            //模拟异常
            int i = 10 / 0;
            return 1024;
        });
        try {
            completableFuture2.whenComplete((t, e) -> {
                System.out.println("------t=" + t);
                System.out.println("------e=" + e);
            }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
