package com.example.javabasic.test;

public class Child extends Father {
    public void methodOne() {
        super.methodOne();
        System.out.println("C");
    }

    public void methodTwo() {
        super.methodTwo();
        System.out.println("D");
    }
}
