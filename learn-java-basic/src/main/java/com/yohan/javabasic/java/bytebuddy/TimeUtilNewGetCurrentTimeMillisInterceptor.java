package com.yohan.javabasic.java.bytebuddy;

import net.bytebuddy.asm.Advice;

/**
 * @author yohan
 * @Date 2025/05/08
 */
public class TimeUtilNewGetCurrentTimeMillisInterceptor {
    @Advice.OnMethodExit
    public static void enter(@Advice.Return(readOnly = false) Long now) {
        System.out.println("TimeUtilNewGetCurrentTimeMillisInterceptor切进来了！now:" + now);

        now = 1111111111111111L;
        System.out.println("TimeUtilNewGetCurrentTimeMillisInterceptor结束了！now:" + now);
    }
}