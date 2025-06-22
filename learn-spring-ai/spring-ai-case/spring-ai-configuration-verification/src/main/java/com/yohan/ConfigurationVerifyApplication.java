package com.yohan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
* @author yohan
* @Date 2025/06/16
*/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ConfigurationVerifyApplication {

	public static void main(String[] args) {

		SpringApplication.run(ConfigurationVerifyApplication.class, args);
	}

}
