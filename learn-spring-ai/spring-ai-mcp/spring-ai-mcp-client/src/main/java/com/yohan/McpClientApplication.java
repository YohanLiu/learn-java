package com.yohan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
* @author yohan
* @Date 2025/06/17
*/
@SpringBootApplication(exclude = {
		org.springframework.ai.mcp.client.autoconfigure.SseHttpClientTransportAutoConfiguration.class,
		DataSourceAutoConfiguration.class
})
public class McpClientApplication {
	public static void main(String[] args) {

		SpringApplication.run(McpClientApplication.class, args);
	}
}
