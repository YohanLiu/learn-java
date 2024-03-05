package com.yohan.javabasic.java.juc.completablefuture;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author yinhou.liu
 * @Date 2024/03/05
 */
public class CompletableFutureApiTest {
    /**
     * get():阻塞直到获取到结果.如果有异常则会抛出.
     * <p>get(long timeout,TimeUnit unit): 阻塞一定的时间后，如果还没获取到结果，就抛异常.获取到结果则输出.
     * <p>join(): 阻塞直到获取到结果.有异常也不会抛出.
     * <p>getNow(T valueIfAbsent): 如果已经完成，则返回结果值，如果没有完成，则返回设定的默认值.立即获取结果不阻塞.
     * <p>complete(T value): 是否打断get方法立即返回括号值.
     */
    @Test
    public void testGet() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            int i = 10 / 0;
            return "get()";
        });
        try {
            System.out.println("get()结果为: " + completableFuture.get());
        } catch (Exception e) {
            System.out.println("get()结果为: 异常了");
        }

        System.out.println("--------------------------------------------------------------");

        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "get(long timeout, TimeUnit unit)";
        });
        try {
            System.out.println("get(long timeout, TimeUnit unit)结果为: " + completableFuture1.get(3, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("--------------------------------------------------------------");

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "join()";
        });
        System.out.println("join()结果为: " + completableFuture2.join());

        System.out.println("--------------------------------------------------------------");

        CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "getNow(T valuelfAbsent)算完了";
        });
//        try {
//            TimeUnit.SECONDS.sleep(1L);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("getNow(T valuelfAbsent)结果为: " + completableFuture3.getNow("没算完返回给定值"));

        System.out.println("--------------------------------------------------------------");

        CompletableFuture<String> completableFuture4 = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "complete(T value)";
        });
        try {
            System.out.println(completableFuture4.complete("completeValue打断 get 获取值") + "\t" + completableFuture4.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * handle --->计算结果存在依赖关系，这两个线程串行化---->有异常也可以往下走一步.
     */
    @Test
    public void testHandle() {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("111");
            return 1;
        }).handle((f, e) -> {
            int i = 10 / 0;
            System.out.println("222");
            return f + 2;
        }).handle((f, e) -> {
            System.out.println("333 f的值:" + f);
            System.out.println("异常为: " + e.getMessage());
            if (Objects.isNull(f)) {
                return 59;
            }
            return f + 3;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("----计算结果： " + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        });

        try {
            System.out.println("testHandle结果为: " + completableFuture.get());
        } catch (Exception e) {
            System.out.println("testHandle结果为: 异常了");
        }
    }

    /**
     * thenApply --->计算结果存在依赖关系，这两个线程串行化---->由于存在依赖关系（当前步错，不走下一步），当前步骤有异常的话就叫停.
     */
    @Test
    public void testThenApply() {
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {
            return f + 2;
        }).thenApply(f -> {
            return f + 3;
        });

        try {
            System.out.println("completableFuture结果为: " + completableFuture.get());
        } catch (Exception e) {
            System.out.println("completableFuture结果为: 异常了");
        }

        System.out.println("--------------------------------------------------------------");

        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("1111");
            return 1;
        }).thenApply(f -> {
            System.out.println("2222");
            int i = 10 / 0;
            return f + 2;
        }).thenApply(f -> {
            System.out.println("3333");
            return f + 3;
        });

        try {
            System.out.println("completableFuture1结果为: " + completableFuture1.get());
        } catch (Exception e) {
            System.out.println("completableFuture1结果为: 异常了");
        }
    }

    /**
     * 对计算结果进行消费.
     * <p>thenRun(Runnable runnable) :任务A执行完执行B，并且不需要A的结果.
     * <p>thenAccept(Consumer action): 任务A执行完执行B，B需要A的结果，但是任务B没有返回值.
     * <p>thenApply(Function fn): 任务A执行完执行B，B需要A的结果，同时任务B有返回值.
     */
    @Test
    public void testConsumer() {
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {
        }).join());
        System.out.println("--------------------------------------------------------------");
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenAccept(r -> System.out.println(r)).join());
        System.out.println("--------------------------------------------------------------");
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenApply(r -> r + " resultB").join());
    }

    /**
     * 对计算速度进行选用,谁快用谁.
     */
    @Test
    public void testApplyToEither() {
        CompletableFuture<String> playA = CompletableFuture.supplyAsync(() -> {
            System.out.println("A come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "playA";
        });

        CompletableFuture<String> playB = CompletableFuture.supplyAsync(() -> {
            System.out.println("B come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "playB";
        });

        CompletableFuture<String> result = playA.applyToEither(playB, f -> {
            return f + " is winer";
        });

        System.out.println(Thread.currentThread().getName() + "  -----: " + result.join());
    }

    /**
     * 对计算结果进行合并.
     * <p>两个CompletableStage任务都完成后，最终能把两个任务的结果一起交给thenCombine来处理.
     * <p>先完成的先等着，等待其他分支任务.
     */
    @Test
    public void testThenCombine() {
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t ---启动");
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        });

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "\t ---启动");
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 20;
        });

        CompletableFuture<Integer> result = completableFuture1.thenCombine(completableFuture2, (x, y) -> {
            System.out.println("-----开始两个结果合并");
            return x + y;
        });

        System.out.println(result.join());
    }
}
