package com.example.javabasic.java.file;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = 8409483118975172480L;

    private String name;

    private Integer age;

    public void show() {
        System.out.println("person.show method: name = " + name + ", age = " + age);
    }
}
