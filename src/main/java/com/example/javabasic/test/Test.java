package com.example.javabasic.test;

import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        String str = "a,b,c,,";
        String[] ary = str.split(",");

        Arrays.stream(ary).forEach(System.out::println);
    }
}
