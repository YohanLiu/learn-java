package com.example.javabasic.java.bytebuddy;

import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author yinhou.liu
 * @Date 2024/01/27
 */
@Service
public class ServiceImpl {


    public boolean serviceMethod(Integer trueOrFalse) {
        System.out.println("ServiceImpl的serviceMethod方法入参:" + trueOrFalse);

        boolean result = serviceStaticMethod(trueOrFalse);

        System.out.println("ServiceImpl的serviceMethod方法返回值:" + result);
        return result;
    }


    private static boolean serviceStaticMethod(Integer trueOrFalse) {
        System.out.println("ServiceImpl的serviceStaticMethod静态方法入参:" + trueOrFalse);
        boolean staticResult = Objects.equals(trueOrFalse, 1);
        System.out.println("ServiceImpl的serviceStaticMethod静态方法返回值:" + staticResult);
        return staticResult;
    }
}
