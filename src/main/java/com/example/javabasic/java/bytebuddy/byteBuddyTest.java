package com.example.javabasic.java.bytebuddy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用 bytebuddy 代理 get 方法.
 * <p>bytebuddy 代理就很灵活,被代理的类无需被 spring 管理,方法也可以是 private.
 *
 * @author yinhou.liu
 * @Date 2024/01/26
 */
@RestController
@RequestMapping("/bytebuddy")
public class byteBuddyTest {
    @GetMapping("/getPrice/{price}")
    public Integer getSmsConfig(@PathVariable("price") Integer price) {
        Fruit fruit = new Fruit();
        fruit.setName("apple");
        fruit.setPrice(price);
        return fruit.getPrice();
    }

}
