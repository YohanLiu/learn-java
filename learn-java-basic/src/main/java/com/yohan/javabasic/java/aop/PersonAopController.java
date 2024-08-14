package com.yohan.javabasic.java.aop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 使用Spring Aspect代理需要被Spring容器管理且方法是 public.
 *
 * @author yinhou.liu
 * @Date 2024/01/22
 */
@RestController
@RequestMapping("/aop")
public class PersonAopController {
    @Resource
    private PersonForAop personForAop;

    /**
     * 如果 aop 想切面到 bean 的 get 方法.
     * <p>被切面的baen需要被 Spring 容器管理,加类似@Component注解.
     *
     * @param name
     * @return
     */
    @GetMapping("/getPersonName/{name}")
    public String getSmsConfig(@PathVariable("name") String name) {
        personForAop.show();
        personForAop.setName(name);
        System.out.println(personForAop.getName());
        return personForAop.getName();
    }
}
