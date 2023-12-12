package com.example.javabasic.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamTest {
    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三"));
        userList.add(new User(2, "李四"));
        userList.add(new User(3, "王五"));
        userList.add(new User(1, "赵六"));

        List<Integer> ageList =
                userList.stream().map(User::getAge).collect(Collectors.toList());
        System.out.println(ageList);
    }
}
