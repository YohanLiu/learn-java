package com.yohan.javabasic.java.juc;


import java.util.concurrent.TimeUnit;

class Phone01
{
    public synchronized void sendEmail()
    {
        System.out.println("-----sendEmail");
    }
    public synchronized void sendSMS()
    {
        System.out.println("-----sendSMS");
    }
}

class Phone02
{
    public synchronized void sendEmail()
    {
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("-----sendEmail");
    }
    public synchronized void sendSMS()
    {
        System.out.println("-----sendSMS");
    }
}

class Phone03
{
    public synchronized void sendEmail()
    {
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("-----sendEmail");
    }
    public void hello()
    {
        System.out.println("-------hello");
    }
}

class Phone05
{
    public static synchronized void sendEmail()
    {
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("-----sendEmail");
    }
    public static synchronized void sendSMS()
    {
        System.out.println("-----sendSMS");
    }
}

class Phone07
{
    public static synchronized void sendEmail()
    {
        try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("-----sendEmail");
    }
    public synchronized void sendSMS()
    {
        System.out.println("-----sendSMS");
    }
}

/**
 * 题目：谈谈你对多线程锁的理解,8锁案例说明.  口诀：线程   操作  资源类
 * <p>8锁案例说明：
 * <p>1 标准访问有ab两个线程，请问先打印邮件还是短信----------sendEmail 然后sendSMS
 * <p>2 sendEmail方法中加入暂停3秒钟，请问先打印邮件还是短信----------sendEmail 然后sendSMS
 * <p>3 添加一个普通的hello方法，请问先打印邮件还是hello----------hello 然后sendEmail
 * <p>4 有两部手机，请问先打印邮件还是短信----------sendSMS 然后sendEmail
 * <p>5 有两个静态同步方法，有1部手机，请问先打印邮件还是短信----------sendEmail 然后sendSMS
 * <p>6 有两个静态同步方法，有2部手机，请问先打印邮件还是短信----------sendEmail 然后sendSMS
 * <p>7 有1个静态同步方法，有1个普通同步方法,有1部手机，请问先打印邮件还是短信----------sendSMS 然后sendEmail
 * <p>8 有1个静态同步方法，有1个普通同步方法,有2部手机，请问先打印邮件还是短信----------sendSMS 然后sendEmail
 * <p>
 * 笔记总结：
 * <p>1-2 对象锁
 * <p>一个对象里面如果有多个synchronized方法，某一个时刻内，只要一个线程去调用其中的一个synchronized方法了，
 * 其它的线程都只能等待，换句话说，某一个时刻内，只能有唯一的一个线程去访问这些synchronized方法
 * {@link 锁的是当前对象this，被锁定后，其它的线程都不能进入到当前对象的其它的synchronized方法}
 * <p>3-4
 * <p>加个普通方法后发现和同步锁无关. {@link 普通方法没申请this对象锁，所以先执行}
 * <p>换成两个对象后，{@link 不是同一把锁了，是两把锁}，情况立刻变化。
 * <p>5-6
 * <p>都换成静态同步方法后，情况又变化
 * <p>三种 synchronized 锁的内容有一些差别:
 * <p>对于普通同步方法，锁的是当前实例对象，通常指this,具体的一部部手机,所有的普通同步方法用的都是同一把锁——>实例对象本身，
 * <p>对于静态同步方法，锁的是当前类的Class对象，如Phone.class唯一的一个模板.一个类的所有实例都共用同一个静态方法.
 * <p>对于同步方法块，锁的是 synchronized 括号内的对象(this或对象实例)，如果括号内是对象.class，则锁的是类的Class对象.
 * <p>7-8
 * <p>当一个线程试图访问同步代码时它首先必须得到锁，正常退出或抛出异常时必须释放锁。
 * <p>所有的普通同步方法用的都是同一把锁——实例对象本身，就是new出来的具体实例对象本身,本类this
 * <p>也就是说如果一个实例对象的普通同步方法获取锁后，该实例对象的其他普通同步方法必须等待获取锁的方法释放锁后才能获取锁。
 * <p>所有的静态同步方法用的也是同一把锁——类对象本身，就是我们说过的唯一模板Class
 * <p>{@link 具体实例对象this和唯一模板Class，这两把锁是两个不同的对象，所以静态同步方法与普通同步方法之间是不会有竞态条件的}
 * <p>但是一旦一个静态同步方法获取锁后，其他的静态同步方法都必须等待该方法释放锁后才能获取锁。
 */
public class Lock8Demo
{
    public static void main(String[] args)//一切程序的入口
    {
        // sendEmail 然后sendSMS
        //lock01();
        // sendEmail 然后sendSMS
        //lock02();

        // hello 然后sendEmail
        //lock03();
        // sendSMS 然后sendEmail
        //lock04();

        // sendEmail 然后sendSMS
        //lock05();
        // sendEmail 然后sendSMS
        //lock06();

        // sendSMS 然后sendEmail
        //lock07();
        // sendSMS 然后sendEmail
        lock08();
    }

    public static void lock01() {
        Phone01 phone01 = new Phone01();
        new Thread(() -> {
            phone01.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone01.sendSMS();
        },"b").start();
    }

    public static void lock02() {
        Phone02 phone02 = new Phone02();
        new Thread(() -> {
            phone02.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone02.sendSMS();
        },"b").start();
    }

    public static void lock03() {
        Phone03 phone03 = new Phone03();
        new Thread(() -> {
            phone03.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone03.hello();
        },"b").start();
    }

    public static void lock04() {
        Phone02 phone = new Phone02();
        Phone02 phone1 = new Phone02();
        new Thread(() -> {
            phone.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone1.sendSMS();
        },"b").start();
    }

    public static void lock05() {
        Phone05 phone05 = new Phone05();
        new Thread(() -> {
            phone05.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone05.sendSMS();
        },"b").start();
    }

    public static void lock06() {
        Phone05 phone = new Phone05();
        Phone05 phone1 = new Phone05();
        new Thread(() -> {
            phone.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone1.sendSMS();
        },"b").start();
    }

    public static void lock07() {
        Phone07 phone07 = new Phone07();
        new Thread(() -> {
            phone07.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone07.sendSMS();
        },"b").start();
    }

    public static void lock08() {
        Phone07 phone = new Phone07();
        Phone07 phone1 = new Phone07();
        new Thread(() -> {
            phone.sendEmail();
        },"a").start();
        //暂停毫秒,保证a线程先启动
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        new Thread(() -> {
            phone1.sendSMS();
        },"b").start();
    }


}





