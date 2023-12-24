package com.example.javabasic.Test;

import org.junit.jupiter.api.Test;

public class ThisTest {
    public static void main(String[] args) {
        System.out.println("main method");
        staticMethod();
        new ThisTest().notStaticMethod();
    }

    @Test
    public void notStatic() {
        this.notStaticMethod();
        ThisTest.staticMethod();
    }

    public static void staticMethod() {
        System.out.println("this is static Method");
    }


    public void notStaticMethod() {
        System.out.println("this is not static Method");
    }

}
