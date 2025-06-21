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
public class RuleService {

	@Tool(description = "这是一个基础路费规则配置查询的工具，该工具会将结果存储在缓存中，并且返回存储的key")
	public String getVehiclePricingRule(@ToolParam(description = "查找规则的开始时间(格式：YYYY-MM-DD HH:mm:ss)") String beginTime,
	                                    @ToolParam(description = "查找规则的结束时间(格式：YYYY-MM-DD HH:mm:ss)") String endTime) {
		// 打印入参
		log.info("beginTime: {}, endTime: {}", beginTime, endTime);
		return "VehiclePricingRule:" + beginTime + endTime;
	}

	@Tool(description = "这是一个查询到付附加费配置，该工具会将结果存储在缓存中，并且返回存储的key")
	public String getDpsRule(@ToolParam(description = "查找到付附加费规则的开始时间(格式：YYYY-MM-DD HH:mm:ss)") String beginTime,
	                         @ToolParam(description = "查找到付附加费规则的结束时间(格式：YYYY-MM-DD HH:mm:ss)") String endTime,
	                         @ToolParam(description = "到付附加费配置业务线枚举。可选值：1，表示小车；2，表示大车") Integer busiLine) {
		// 打印入参
		log.info("beginTime: {}, endTime: {}, busiLine: {}", beginTime, endTime, busiLine);
		return "VehiclePricingRule:" + beginTime + endTime+ busiLine;
	}
}
