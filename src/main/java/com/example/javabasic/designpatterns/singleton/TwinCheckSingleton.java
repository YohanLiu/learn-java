package com.example.javabasic.designpatterns.singleton;

/**
 * 双重检测实现单例模式
 *
 * @author yinhou.liu
 * @Date 2024/01/05
 */
public class TwinCheckSingleton {

    private static volatile TwinCheckSingleton twinCheckSingleton;

    private TwinCheckSingleton() {
    }

    public static TwinCheckSingleton getSingleton() {
        if (twinCheckSingleton == null) {
            synchronized (TwinCheckSingleton.class) { // 此处为类级别的锁
                if (twinCheckSingleton == null) {
                    twinCheckSingleton = new TwinCheckSingleton();
                }
            }
        }
        return twinCheckSingleton;
    }
}
