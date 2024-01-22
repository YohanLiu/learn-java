package com.example.javabasic.java.aop;

import com.example.javabasic.java.reflect.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 使用Spring Aspect代理需要被Spring容器管理.
 *
 * @author yinhou.liu
 * @Date 2024/01/22
 */
@RestController
@RequestMapping("/aop")
public class PersonAopTest {
    @Resource
    private Person person;

    @GetMapping("/getPersonName")
    public String getSmsConfig() {
        person.show();
        person.setName("张三1");
        System.out.println(person.getName());
        return person.getName();
    }
}
