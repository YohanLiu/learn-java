package com.yohan.javabasic.java.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author yinhou.liu
 * @Date 2024/01/22
 */
@Aspect
@Component
@Slf4j
public class PersonAop {
    @Pointcut("execution(* com.yohan.javabasic.java.aop.PersonForAop.getName())")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String proceed = (String) joinPoint.proceed();
        if (StringUtils.equals(proceed, "张三")) {
            return "张三" + "---AOP equals 张三";
        } else {
            return proceed + "---AOP not equals 张三";
        }
    }
}
