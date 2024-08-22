package com.yohan.javabasic.java.aop;

import com.yohan.javabasic.utils.SpringUtil;
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

    /**
     * 测试对同一个方法有多个切面的执行顺序.
     * <p>如果没用order注解指定顺序，就会默认用类的加载顺序.
     * <p>前一个切面执行joinPoint.proceed();的，下一个aop就会执行.
     * <p>前一个执行return '结果'的，下一个就不执行了
     */
    @GetMapping("/twoAop/order")
    public String getSmsConfig() {
        PersonAopController personAopController = SpringUtil.getBean(PersonAopController.class);
        return personAopController.defaultReturn();
    }

    public String defaultReturn() {
        System.out.println("defaultReturn");
        return "defaultReturn";
    }
}
