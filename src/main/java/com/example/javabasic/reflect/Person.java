package com.example.javabasic.reflect;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
        System.out.println("Person(" + name + ", " +age + ")有参构造器");
    }

    // 方法
    public void show() {
        System.out.println("你好，我是一个Person");
    }

    private String showNation(String nation) {
        return "我的国籍是：" + nation;
    }
}
