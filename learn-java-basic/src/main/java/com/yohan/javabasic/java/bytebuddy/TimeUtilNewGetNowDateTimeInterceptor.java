package com.yohan.javabasic.java.bytebuddy;

import com.yohan.javabasic.utils.TimeUtil;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

/**
 * @author yohan
 * @Date 2025/05/08
 */
public class TimeUtilNewGetNowDateTimeInterceptor {
    @Advice.OnMethodExit
    public static void enter(@Advice.Return(readOnly = false) LocalDateTime now) {
        System.out.println("TimeUtilNewGetNowDateTimeInterceptor切进来了！now:" + now);
        now = TimeUtil.longSecTOLocalDateTime(111111111L);
        System.out.println("TimeUtilNewGetNowDateTimeInterceptor切出来了！now:" + now);
    }
}
