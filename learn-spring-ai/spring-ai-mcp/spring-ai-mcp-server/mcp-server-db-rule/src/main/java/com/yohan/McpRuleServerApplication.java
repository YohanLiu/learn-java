package com.yohan;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
* @author yohan
* @Date 2025/06/17
*/
@SpringBootApplication
public class McpRuleServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(McpRuleServerApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider ruleTools(RuleService ruleService) {
		return MethodToolCallbackProvider.builder().toolObjects(ruleService).build();
	}

	public record TextInput(String input) {
	}

	/**
	 * 将输入字母转为大写的 MCP Tools
	 */
	@Bean
	public ToolCallback toUpperCase() {
		return FunctionToolCallback.builder("toUpperCase", (TextInput input) -> input.input().toUpperCase())
				.inputType(TextInput.class)
				.description("Put the text to upper case")
				.build();
	}
}
