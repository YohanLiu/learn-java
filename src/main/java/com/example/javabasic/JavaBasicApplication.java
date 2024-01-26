package com.example.javabasic;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.example.javabasic.java.bytebuddy.ByteBuddyAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApolloConfig
@Slf4j
public class JavaBasicApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(JavaBasicApplication.class);
        application.addInitializers(new ByteBuddyAspect());
        application.run(args);
        log.info("启动耗时:{}", System.currentTimeMillis() - t1);
    }
}
