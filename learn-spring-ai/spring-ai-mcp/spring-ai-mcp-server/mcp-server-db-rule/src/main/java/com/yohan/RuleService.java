package com.yohan;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
* @author yohan
* @Date 2025/06/17
*/
@Service
public class RuleService {

	/**
	 * 根据时间获取基础里程定价规则，以json字符串返回
	 *
	 * @param time 时间
	 * @return 基础里程定价规则，以json字符串返回
	 */
	@Tool(description = "根据时间获取基础里程定价规则，以json字符串返回")
	public String getVehiclePricingRule(LocalDateTime time) {
		System.out.println("getVehiclePricingRule:" + time);
		return "VehiclePricingRule:" + time;
	}
}
