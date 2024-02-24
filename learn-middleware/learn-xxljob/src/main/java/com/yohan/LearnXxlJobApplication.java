package com.yohan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * xxljob部署方式通过github下载xxljob代码启动xxl-job-admin.
 * <p>控制台访问网址就是xxl-job-admin启动后的地址.一般为：http://localhost:8080/xxl-job-admin.
 */
@SpringBootApplication
@Slf4j
public class LearnXxlJobApplication {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        SpringApplication application = new SpringApplication(LearnXxlJobApplication.class);
        application.run(args);
        log.info("learn-xxljob模块启动耗时:{}ms", System.currentTimeMillis() - t1);
    }
}