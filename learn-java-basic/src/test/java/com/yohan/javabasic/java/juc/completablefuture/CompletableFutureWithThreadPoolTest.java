package com.yohan.javabasic.java.juc.completablefuture;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yinhou.liu
 * @Date 2024/03/05
 */
public class CompletableFutureWithThreadPoolTest {
    /**
     * 如果没有传入自定义线程池，都用默认线程池ForkJoinPool.
     */
    @Test
    public void test01() {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1号任务" + "\t" + Thread.currentThread().getName());
            return "abcd";
        }).thenRunAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("3号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("4号任务" + "\t" + Thread.currentThread().getName());
        });
        try {
            System.out.println("结果为:" + completableFuture.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 传入一个线程池，如果你执行第一个任务时，传入了一个自定义线程池.
     * <p>调用thenRunAsync执行第二个任务时，则第一个任务使用的是你自定义的线程池，第二个任务使用的是ForkJoin线程池.
     */
    @Test
    public void test02() {
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,
                5,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1号任务" + "\t" + Thread.currentThread().getName());
            return "abcd";
        }, threadPool).thenRunAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("3号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("4号任务" + "\t" + Thread.currentThread().getName());
        });
        try {
            System.out.println("结果为:" + completableFuture.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        threadPool.shutdown();
    }

    /**
     * 传入一个线程池，如果你执行第一个任务时，传入了一个自定义线程池.
     * <p>调用thenRun方法执行第二个任务时，则第二个任务和第一个任务时共用同一个线程池.
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

        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1号任务" + "\t" + Thread.currentThread().getName());
            return "abcd";
        }, threadPool).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("3号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("4号任务" + "\t" + Thread.currentThread().getName());
        });
        try {
            System.out.println("结果为:" + completableFuture.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        threadPool.shutdown();
    }

    /**
     * 线程处理太快,系统优化切换原则,直接使用main线程处理.
     * <p>效果需要用 debug 模式启动才能看到.
     */
    @Test
    public void test04() {
        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("1号任务" + "\t" + Thread.currentThread().getName());
            return "abcd";
        }).thenRunAsync(() -> {

            System.out.println("2号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {

            System.out.println("3号任务" + "\t" + Thread.currentThread().getName());
        }).thenRun(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("4号任务" + "\t" + Thread.currentThread().getName());
        });
        try {
            System.out.println("结果为:" + completableFuture.get(2L, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
