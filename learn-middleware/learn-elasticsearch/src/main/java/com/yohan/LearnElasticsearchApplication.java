package com.yohan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 需要es启动起来,此模块才能启动起来.es和kibana部署可以通过docker来部署.
 * <p>kibana默认端口：5601.访问网址:ip+port.本地就是localhost:5601.
 */
@SpringBootApplication
@Slf4j
public class LearnElasticsearchApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(LearnElasticsearchApplication.class);
        application.run(args);
        log.info("learn-elasticsearch模块启动耗时:{}ms", System.currentTimeMillis() - t1);
    }
}