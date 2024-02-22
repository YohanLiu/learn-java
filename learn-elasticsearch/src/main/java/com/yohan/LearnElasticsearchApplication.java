package com.yohan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class LearnElasticsearchApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(LearnElasticsearchApplication.class);
        application.run(args);
        log.info("启动耗时:{}", System.currentTimeMillis() - t1);
    }
}