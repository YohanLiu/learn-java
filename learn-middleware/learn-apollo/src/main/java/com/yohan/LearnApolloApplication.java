package com.yohan;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * apollo的部署可以通过docker来进行部署。
 * <p>apollo页面默认端口：8070.访问网址:ip+port.本地就是localhost:8070.
 * <p>初始账户：apollo，密码：admin.
 */
@SpringBootApplication
@EnableApolloConfig
@Slf4j
public class LearnApolloApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(LearnApolloApplication.class);
        application.run(args);
        log.info("learn-apollo模块启动耗时:{}ms", System.currentTimeMillis() - t1);
    }
}