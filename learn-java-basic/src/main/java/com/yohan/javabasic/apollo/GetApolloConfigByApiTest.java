package com.yohan.javabasic.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

/**
 * @author yohan
 * @Date 2023/12/16
 */
public class GetApolloConfigByApiTest {
    public static void main(String[] args) {
        // 设置系统参数
        System.setProperty("app.id", "apollo-quickstart");
        System.setProperty("env", "dev");
        System.setProperty("apollo.configService", "http://localhost:8080");

        Config appConfig = ConfigService.getAppConfig();
        String property = appConfig.getProperty("yohan.key", null);
        System.out.println(property);
    }
}
