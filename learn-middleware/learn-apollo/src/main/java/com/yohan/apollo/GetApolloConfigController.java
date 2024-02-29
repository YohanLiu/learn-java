package com.yohan.apollo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yohan
 * @Date 2023/12/16
 */
@RestController
@RequestMapping("/apollo")
public class GetApolloConfigController {
    // 通过注解获取apollo中的配置，没有赋值默认值false
    @Value("${yohan.key:default by @Value}")
    private String yohanKey;

    @Value("${yohan.value:default by @Value}")
    private String yohanValue;

    @GetMapping("/yohankeyandvalue")
    public String getSmsConfig() {
        System.out.println("yohanKey:" + yohanKey);
        System.out.println("yohanValue:" + yohanValue);
        String res = "yohanKey:" + yohanKey + "\nyohanValue:" + yohanValue;
        return res.replaceAll("\n", "<br/>");
    }
}
