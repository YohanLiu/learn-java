package com.example.javabasic.java.bytebuddy;

import net.bytebuddy.asm.Advice;

/**
 * Fruit.getPrice()方法退出拦截器.
 *
 * @author yinhou.liu
 * @Date 2024/01/26
 */
public class FruitInterceptor {
    /**
     * Fruit.getPrice()方法退出拦截器.
     *
     * @param currentInstance 当前对象
     * @param price           代理方法之前的返回结果
     */
    @Advice.OnMethodExit
    public static void enter(@Advice.This Object currentInstance, @Advice.Return(readOnly = false) Integer price) {
        Fruit fruit = (Fruit) currentInstance;
        // 这里不可调用该类被代理的 get 方法,会一直递归导致异常java.lang.StackOverflowError
        // System.out.println("byte buddy Fruit price 之前结果: " + fruit.getPrice());
        System.out.println("byte buddy Fruit name: " + fruit.getName());
        System.out.println("byte buddy Fruit price 之前结果: " + price);

        if (price <= 520) {
            price = 520;
        } else {
            price = 5211314;
        }
    }
}
