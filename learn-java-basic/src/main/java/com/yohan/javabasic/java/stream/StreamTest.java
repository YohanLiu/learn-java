package com.yohan.javabasic.java.stream;

import org.apache.commons.lang3.tuple.MutablePair;
import com.yohan.javabasic.java.stream.User;
import java.util.ArrayList;
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

        List<MutablePair<String, Double>> pairArrayList = new ArrayList<>(3);
        pairArrayList.add(new MutablePair<>("version", 12.10));
        pairArrayList.add(new MutablePair<>("version", 12.19));
        pairArrayList.add(new MutablePair<>("version", 6.28));

        // 生成的 map 集合中只有一个键值对：{version=6.28}
        Map<String, Double> map = pairArrayList
                .stream()
                .collect(Collectors
                        .toMap(MutablePair::getKey, MutablePair::getValue, (v1, v2) -> v2));

        System.out.println(map);
    }
}
