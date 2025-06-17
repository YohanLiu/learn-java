package com.yohan;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
* @author yohan
* @Date 2025/06/17
*/
@Service
public class LarkService {
	/**
	 * 获取飞书文档链接的内容，以json字符串返回
	 *
	 * @param url 飞书文档链接
	 * @return 飞书文档链接的内容，json字符串格式返回
	 */
	@Tool(description = "获取飞书文档链接的内容，以json字符串返回")
	public String getLarkContext(String url) {
		System.out.println("Lark url: " + url);
		return "Lark context: " + url;
	}
}
