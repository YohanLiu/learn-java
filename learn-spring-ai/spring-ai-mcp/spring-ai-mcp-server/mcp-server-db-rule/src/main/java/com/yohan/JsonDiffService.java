package com.yohan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * @author yohan
 * @Date 2025/06/23
 */
@Service
@Slf4j
public class JsonDiffService {
	@Tool(description = "json 对比工具")
	public String jsonDiff(@ToolParam(description = "运营预期json 数据") String expectKey,
	                       @ToolParam(description = "数据库实际查询 json 数据") String actualKey) {
		log.info("expectKey: {}, actualKey: {}", expectKey, actualKey);
		double random = Math.random();

		if (random > 0.5) {
			log.info("一条有差异");
			return "{\"is_identical\":false,\"message\":{\"consensus_count\":2,\"disconsensus_count\":1," +
					"\"summary\":\"结论:核验失败。运营输入3条，后台配置3条,一致数量2条，不一致数量1条。\",\"expected_count\":3,\"actual_count\":3},\"differences\":{\"values_changed\":{\"root[2]['startSegKm']\":{\"new_value\":25,\"old_value\":5}}}}";
		} else {
			log.info("无差异");
			return "{\"differences\":{},\"is_identical\":true,\"message\":{\"consensus_count\":3," +
					"\"disconsensus_count\":0,\"summary\":\"结论:核验成功。运营输入3条，后台配置3条,一致数量3条，不一致数量0条。\",\"expected_count\":3,\"actual_count\":3}}";
		}
	}
}
