package com.yohan.javabasic;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.yohan.javabasic.java.bytebuddy.ByteBuddyAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApolloConfig
@Slf4j
public class LearnJavaBasicApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(LearnJavaBasicApplication.class);
        application.addInitializers(new ByteBuddyAspect());
        application.run(args);
        log.info("启动耗时:{}", System.currentTimeMillis() - t1);
    }
}
