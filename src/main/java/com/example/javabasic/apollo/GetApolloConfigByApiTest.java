package com.example.javabasic.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

public class GetApolloConfigByApiTest {
    public static void main(String[] args) {
        // 设置系统参数
        System.setProperty("app.id", "apollo-quickstart");
        System.setProperty("env", "dev");
        System.setProperty("apollo.configService", "http://localhost:8080");

        Config appConfig = ConfigService.getAppConfig();
        String property = appConfig.getProperty("sms.enable", null);
        System.out.println(property);
    }
}
