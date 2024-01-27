package com.example.javabasic.java.bytebuddy;

import lombok.Data;

/**
 * @author yinhou.liu
 * @Date 2024/01/26
 */
@Data
public class Fruit {
    private String name;

    private Integer price;

    public static Integer getSellByDate() {
        return 100;
    }
}
