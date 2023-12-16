package com.example.javabasic.apollo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apollo")
public class GetApolloConfigController {
    // 通过注解获取apollo中的配置，没有赋值默认值false
    @Value("${sms.enable:false}")
    private String smsEnable;

    @GetMapping("/sms-enable")
    public String getSmsConfig() {
        return smsEnable;
    }
}
