package com.yohan.javabasic.designpatterns.singleton;

/**
 * 双重检测实现单例模式
 *
 * @author yinhou.liu
 * @Date 2024/01/05
 */
public class TwinCheckSingleton {

    // 如果不加volatile关键字，可能会出现指令重排的情况，导致在实例未完全初始化时就被访问就为null
    // 加volatile关键字可以禁止指令重排
    private static volatile TwinCheckSingleton twinCheckSingleton;

    private TwinCheckSingleton() {
    }

    public static TwinCheckSingleton getSingleton() {
        if (twinCheckSingleton == null) {
            synchronized (TwinCheckSingleton.class) { // 此处为类级别的锁
                if (twinCheckSingleton == null) {
                    // 此行代码有三步,1.分配内存空间,2.初始化对象,3.将对象指向分配的内存空间
                    // 如果没有volatile关键字,可能会出现指令重排的情况,就出现1.分配内存空间,2.将对象指向分配的内存空间,3.初始化对象
                    // 那此时对象没被初始化所以就为null了
                    twinCheckSingleton = new TwinCheckSingleton();
                }
            }
        }
        return twinCheckSingleton;
    }
}
