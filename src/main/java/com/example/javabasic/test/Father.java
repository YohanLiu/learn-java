package com.example.javabasic.test;

import java.util.logging.SocketHandler;

public class Father {
    public void methodOne() {
        System.out.println("A");
        methodTwo();
    }

    public void methodTwo() {
        System.out.println("B");
    }

    public Father() {
        System.out.println("B");
    }

    public int B() {
        System.out.println(100);
        return 100;
    }

    public Father(String msg) {
        this();
        System.out.println("msg");
    }
}
