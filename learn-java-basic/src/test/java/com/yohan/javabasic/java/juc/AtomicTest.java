package com.yohan.javabasic.java.juc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.function.LongBinaryOperator;

/**
 * @author yohan
 * @Date 2024/03/16
 */
public class AtomicTest {

    // 基本类型原子类------------------------------------------------------------------------------------------------------
    class MyNumber {
        AtomicInteger atomicInteger = new AtomicInteger();

        public void addPlusPlus() {
            atomicInteger.getAndIncrement();
        }
    }

    @Test
    public void atomicIntegerTest() {
        int size = 50;
        MyNumber myNumber = new MyNumber();
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        myNumber.addPlusPlus();
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }

        //等待上面50个线程全部计算完成后，再去获得最终值
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Thread.currentThread().getName() + "\t" + "result: " + myNumber.atomicInteger.get());
    }

    // 数组类型原子类------------------------------------------------------------------------------------------------------
    @Test
    public void atomicIntegerArrayTest() {
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[5]);
        //AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(5);
        //AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1,2,3,4,5});

        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.println("下标" + i + "的值：" + atomicIntegerArray.get(i));
        }

        System.out.println();

        int tmpInt;

        tmpInt = atomicIntegerArray.getAndSet(0, 1122);
        System.out.println("原始值：" + tmpInt + "\t" + "根据下标修改后值：" + atomicIntegerArray.get(0));

        tmpInt = atomicIntegerArray.getAndIncrement(0);
        System.out.println("原始值：" + tmpInt + "\t" + "自增后值：" + atomicIntegerArray.get(0));
    }


    // 引用类型原子类------------------------------------------------------------------------------------------------------
    @Getter
    @ToString
    @AllArgsConstructor
    class User {
        String userName;
        Integer age;
    }

    /**
     * 利用AtomicReference实现自旋锁.{@link SpinLockDemo}
     */
    @Test
    public void atomicReferenceTest() {
        AtomicReference<User> atomicReference = new AtomicReference<>();

        User z3 = new User("z3", 22);
        User li4 = new User("li4", 28);

        atomicReference.set(z3);

        System.out.println(atomicReference.compareAndSet(z3, li4) + "\t" + atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(z3, li4) + "\t" + atomicReference.get().toString());
    }

    @Test
    public void atomicStampedReferenceTest() {
        User z3 = new User("z3", 22);

        AtomicStampedReference<User> stampedReference = new AtomicStampedReference<>(z3, 1);

        System.out.println("初始：" + stampedReference.getReference() + "\t" + stampedReference.getStamp());

        User li4 = new User("li4", 28);

        boolean b;
        b = stampedReference.compareAndSet(z3, li4, stampedReference.getStamp(), stampedReference.getStamp() + 1);

        System.out.println("改成li4：" + b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());

        b = stampedReference.compareAndSet(li4, z3, stampedReference.getStamp(), stampedReference.getStamp() + 1);

        System.out.println("改成z3：" + b + "\t" + stampedReference.getReference() + "\t" + stampedReference.getStamp());
    }

    @Test
    public void atomicMarkableReferenceTest() {
        AtomicMarkableReference markableReference = new AtomicMarkableReference(100, false);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName() + "\t" + "默认标识：" + marked);
            //暂停1秒钟线程,等待后面的T2线程和我拿到一样的模式flag标识，都是false
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            markableReference.compareAndSet(100, 1000, marked, !marked);
            // 还是会出现ABA的问题
            //markableReference.compareAndSet(markableReference.getReference(), 100, !marked, marked);
            countDownLatch.countDown();
        }, "t1").start();

        new Thread(() -> {
            boolean marked = markableReference.isMarked();
            System.out.println(Thread.currentThread().getName() + "\t" + "默认标识：" + marked);

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean b = markableReference.compareAndSet(100, 2000, marked, !marked);
            System.out.println(Thread.currentThread().getName() + "\t" + "t2线程CASresult： " + b);
            System.out.println(Thread.currentThread().getName() + "\t" + markableReference.isMarked());
            System.out.println(Thread.currentThread().getName() + "\t" + markableReference.getReference());
            countDownLatch.countDown();
        }, "t2").start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 对象的属性修改原子类-------------------------------------------------------------------------------------------------

    class BankAccount {
        String bankName = "CCB";

        //更新的对象属性必须使用 public volatile 修饰符。
        public volatile int money = 0;

        public synchronized void add() {
            money++;
        }

        //因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须
        // 使用静态方法newUpdater()创建一个更新器，并且需要设置想要更新的类和属性。
        AtomicIntegerFieldUpdater<BankAccount> fieldUpdater =
                AtomicIntegerFieldUpdater.newUpdater(BankAccount.class, "money");

        //不加synchronized，保证高性能原子性，局部微创小手术
        public void transMoney(BankAccount bankAccount) {
            fieldUpdater.getAndIncrement(bankAccount);
        }
    }

    /**
     * 10个线程,每个线程转账1000，
     * <p>不使用synchronized,尝试使用AtomicIntegerFieldUpdater来实现.
     */
    @Test
    public void atomicIntegerFieldUpdaterTest() {
        BankAccount bankAccount = new BankAccount();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 1000; j++) {
                        //bankAccount.add();
                        bankAccount.transMoney(bankAccount);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Thread.currentThread().getName() + "\t" + "result: " + bankAccount.money);
    }

    class MyInit {
        public volatile Boolean isInit = Boolean.FALSE;

        AtomicReferenceFieldUpdater<MyInit, Boolean> referenceFieldUpdater =
                AtomicReferenceFieldUpdater.newUpdater(MyInit.class, Boolean.class, "isInit");

        public void init(MyInit myInit) {
            if (referenceFieldUpdater.compareAndSet(myInit, Boolean.FALSE, Boolean.TRUE)) {
                System.out.println(Thread.currentThread().getName() + "\t" + "----- start init,need 2 seconds");
                //暂停几秒钟线程
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t" + "----- over init");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t" + "----- 已经有线程在进行初始化工作。。。。。");
            }
        }
    }

    /**
     * 多线程并发调用一个类的初始化方法，如果未被初始化过，将执行初始化工作，
     * <p>要求只能被初始化一次，只有一个线程操作成功.
     */
    @Test
    public void atomicReferenceFieldUpdaterTest() {
        MyInit myInit = new MyInit();
        int size = 5;
        CountDownLatch countDownLatch = new CountDownLatch(size);

        for (int i = 1; i <= size; i++) {
            new Thread(() -> {
                myInit.init(myInit);
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 原子操作增强类------------------------------------------------------------------------------------------------------
    @Test
    public void longAdderApiTest() {
        LongAdder longAdder = new LongAdder();

        longAdder.increment();
        longAdder.increment();
        longAdder.increment();

        System.out.println("longAdder结果：" + longAdder.sum());

        LongAccumulator longAccumulator = new LongAccumulator(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return left + right;
            }
        }, 0);

        longAccumulator.accumulate(1);//1
        longAccumulator.accumulate(3);//4

        System.out.println("longAccumulator结果：" + longAccumulator.get());
    }

    class ClickNumber {
        int number = 0;

        public synchronized void clickBySynchronized() {
            number++;
        }

        AtomicLong atomicLong = new AtomicLong(0);

        public void clickByAtomicLong() {
            atomicLong.getAndIncrement();
        }

        LongAdder longAdder = new LongAdder();

        public void clickByLongAdder() {
            longAdder.increment();
        }

        LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);

        public void clickByLongAccumulator() {
            longAccumulator.accumulate(1);
        }
    }

    @Test
    public void testWhoFast() throws InterruptedException {

        int num = 10000000;
        int threadNumber = 50;

        ClickNumber clickNumber = new ClickNumber();
        long startTime;
        long endTime;

        CountDownLatch countDownLatch1 = new CountDownLatch(threadNumber);
        CountDownLatch countDownLatch2 = new CountDownLatch(threadNumber);
        CountDownLatch countDownLatch3 = new CountDownLatch(threadNumber);
        CountDownLatch countDownLatch4 = new CountDownLatch(threadNumber);

        startTime = System.currentTimeMillis();
        for (int i = 1; i <= threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= num; j++) {
                        clickNumber.clickBySynchronized();
                    }
                } finally {
                    countDownLatch1.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch1.await();
        endTime = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime - startTime) + " 毫秒" + "\t clickBySynchronized: " + clickNumber.number);

        startTime = System.currentTimeMillis();
        for (int i = 1; i <= threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= num; j++) {
                        clickNumber.clickByAtomicLong();
                    }
                } finally {
                    countDownLatch2.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch2.await();
        endTime = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime - startTime) + " 毫秒" + "\t clickByAtomicLong: " + clickNumber.atomicLong.get());


        startTime = System.currentTimeMillis();
        for (int i = 1; i <= threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= num; j++) {
                        clickNumber.clickByLongAdder();
                    }
                } finally {
                    countDownLatch3.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch3.await();
        endTime = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime - startTime) + " 毫秒" + "\t clickByLongAdder: " + clickNumber.longAdder.sum());

        startTime = System.currentTimeMillis();
        for (int i = 1; i <= threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 1; j <= num; j++) {
                        clickNumber.clickByLongAccumulator();
                    }
                } finally {
                    countDownLatch4.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch4.await();
        endTime = System.currentTimeMillis();
        System.out.println("----costTime: " + (endTime - startTime) + " 毫秒" + "\t clickByLongAccumulator: " + clickNumber.longAccumulator.get());
    }
}
