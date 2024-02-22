package com.yohan.javabasic.java.aop;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PersonForAop {
    private String name;

    public int age;

    // 构造器
    public PersonForAop() {
        System.out.println("PersonForAop()无参构造器");
    }

    private PersonForAop(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("PersonForAop(" + name + ", " + age + ")有参构造器");
    }

    // 方法
    public void show() {
        System.out.println("你好，我是一个Person");
    }
}
