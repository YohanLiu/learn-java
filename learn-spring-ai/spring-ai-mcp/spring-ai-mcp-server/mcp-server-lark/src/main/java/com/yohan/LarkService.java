package com.yohan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
* @author yohan
* @Date 2025/06/17
*/
@Service
@Slf4j
public class LarkService {

	@Tool(description = "读取飞书Excel数据,并返回缓存key")
	public String getLarkContext(@ToolParam(description = "飞书Excel表格URL") String excelUrl,
	                             @ToolParam(description = "读取行索引行，默认为3") String readLineIndex,
	                             @ToolParam(description = "读取飞书模板id（id" +
			                             "值：基础定价、搬家业务服务费、到付附加费、特快、议价用车用户出价、拼车用户出价），每次请求只允许传递一个模版id") String tempId) {
		// 入参打印日志
		log.info("excelUrl: {}, readLineIndex: {}, tempId: {}", excelUrl, readLineIndex, tempId);
		return "Lark context: " + excelUrl + readLineIndex + tempId;
	}
}
