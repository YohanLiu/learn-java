package com.yohan.javabasic.java.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class defaultReturnAop2 {
    @Pointcut("execution(* com.yohan.javabasic.java.aop.PersonAopController.defaultReturn())")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("defaultReturnAop2");
        return "defaultReturnAop2";
    }
}