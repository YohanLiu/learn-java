package com.example.javabasic.java.reflect;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Person {
    private String name;

    public int age;

    // 构造器
    public Person() {
        System.out.println("Person()无参构造器");
    }

    private Person(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("Person(" + name + ", " + age + ")有参构造器");
    }

    // 方法
    public void show() {
        System.out.println("你好，我是一个Person");
    }

    private String showNation(String nation) {
        return "我的国籍是：" + nation;
    }
}
