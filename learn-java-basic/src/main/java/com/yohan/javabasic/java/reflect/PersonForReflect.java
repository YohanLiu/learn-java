package com.yohan.javabasic.java.reflect;

import lombok.Data;

@Data
public class PersonForReflect {
    private String name;

    public int age;

    // 构造器
    public PersonForReflect() {
        System.out.println("PersonForReflect()无参构造器");
    }

    private PersonForReflect(String name, int age) {
        this.name = name;
        this.age = age;
        System.out.println("PersonForReflect(" + name + ", " + age + ")有参构造器");
    }

    // 方法
    public void show() {
        System.out.println("你好，我是一个Person");
    }

    private String showNation(String nation) {
        return "我的国籍是：" + nation;
    }
}
