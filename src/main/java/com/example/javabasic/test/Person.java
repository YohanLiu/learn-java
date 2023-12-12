package com.example.javabasic.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

public class Person {

    public static void main(String[] args) {
        String[] words = {"apple", "pear", "banana", "apple"};
        HashMap<String, Integer> time = new LinkedHashMap<>();
        for (int i = 0; i < words.length; i++) {
            if (time.containsKey(words[i])) {
                time.put(words[i], time.get(words[i]) + 1);
            } else {
                time.put(words[i], 1);
            }
        }

        StringJoiner sj = new StringJoiner(",");
        for (String key : time.keySet()) {
            sj.add(key + ":" + time.get(key));
        }
        System.out.println(sj);

    }


}
