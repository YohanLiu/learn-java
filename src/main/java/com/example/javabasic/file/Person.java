package com.example.javabasic.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = 8409483118975172480L;

    private String name;

    private Integer age;
}
