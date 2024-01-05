package com.example.javabasic.designpatterns;

import com.example.javabasic.designpatterns.singleton.EnumSingleton;
import com.example.javabasic.designpatterns.singleton.TwinCheckSingleton;
import org.junit.jupiter.api.Test;

/**
 * 单例模式测试类.
 *
 * @author yinhou.liu
 * @Date 2024/01/05
 */
public class SingletonTest {
    @Test
    public void testSingleton() {
        EnumSingleton instance = EnumSingleton.getInstance();
        EnumSingleton instance1 = EnumSingleton.getInstance();
        System.out.println(instance1 == instance);

        TwinCheckSingleton singleton = TwinCheckSingleton.getSingleton();
        TwinCheckSingleton singleton1 = TwinCheckSingleton.getSingleton();
        System.out.println(singleton1 == singleton);
    }
}
