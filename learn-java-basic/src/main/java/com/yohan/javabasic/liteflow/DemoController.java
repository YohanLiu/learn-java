package com.yohan.javabasic.liteflow;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/liteflow")
public class DemoController {

    @Resource
    private DemoClass demoClass;

    @GetMapping("/test")
    public String testConfig() {
        demoClass.testConfig();
        return "ok";
    }
}
