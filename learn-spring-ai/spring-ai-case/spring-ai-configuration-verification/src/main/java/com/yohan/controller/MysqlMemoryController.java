package com.yohan.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 基于mysql的记忆聊天.
 *
 * @author yohan
 */
@RestController
@RequestMapping("/advisor/memory/chat")
public class MysqlMemoryController {

	private final ChatClient chatClient;

	private final int MAX_MESSAGES = 100;

	private final MessageWindowChatMemory messageWindowChatMemory;

    private static final String DEFAULT_PROMPT =
            "你现在是一个价格配置核验工具，目前支持基础定价的核验、搬家业务服务费、到付附加费、特快系数和用户出价配置的配置核验（统称为配置核验），其他价格核验暂不支持。回复的时候使用✅或者❌来显示执行结果。如果用户只输入参数不全，则提示用户继续输入。\n" +
                    "配置核验步骤分为以下几步：\n" +
                    "1、根据用户提供的运营配置飞书链接、tempId根据用户输入核验进行识别、表格行索引（默认3），然后调用读取飞书Excel返回缓存key工具获得。\n" +
                    "2、根据用户提供的开始日期(beginTime)、结束日期时间(endTime)（格式，例如：2025-05-07 " +
                    "00:00:00）当做入参调用获取基础车型定价工具、搬家业务服务费数据、到付附加费的配置（其中到付附加费会多一个额外的入参业务线busiLine" +
                    "（大车/小车）、用户出价会多一个额外的入参商品类型commodityPrdCateEnum）存缓存获得actualKey，一定要注意，如果用户没填时间，不进行下一步。\n" +
                    "3、使用expectKey、actualKey请求从json 对比工具DeepDiff版并输出。\n" +
                    "        3-1、必须输出（data->output->message->summary）中原本的结论；\n" +
                    "        3-2、以表格（行号、字段、运营输入、核验结果）形式输出变更的（data->output->differences->values_changed" +
                    "，数组下标表示运营预期数据行号，结合表格行索引加和后输出）；\n" +
                    "        3-3、以表格（表格里不需要输出行号）形式输出新增的（data->output->differences->iterable_item_added）";

	public MysqlMemoryController(ChatClient.Builder builder, MysqlChatMemoryRepository mysqlChatMemoryRepository,
                                 ToolCallbackProvider tools, @Qualifier("redisVectorStoreCustom") RedisVectorStore redisVectorStore) {
		this.messageWindowChatMemory = MessageWindowChatMemory.builder()
				.chatMemoryRepository(mysqlChatMemoryRepository)
				.maxMessages(MAX_MESSAGES)
				.build();

		// 创建检索增强顾问
		Advisor documentAdvisor = RetrievalAugmentationAdvisor.builder()
				.documentRetriever(VectorStoreDocumentRetriever.builder()
						.vectorStore(redisVectorStore)
						.similarityThreshold(0.5)       // 相似度阈值
						.topK(1)                        // 返回文档数量
						.build())
				.build();

		this.chatClient = builder
				.defaultToolCallbacks(tools)
				.defaultSystem(DEFAULT_PROMPT)
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
								.build(), documentAdvisor
				)
				.defaultOptions(DashScopeChatOptions.builder()
						.withTemperature(0.0)
						.build())
				.build();
	}

	@GetMapping("/call")
	public String call(@RequestParam(value = "query", defaultValue = "你好，我的外号是金子，请记住呀") String query,
	                   @RequestParam(value = "conversation_id", defaultValue = "jinzi") String conversationId
	) {
		return chatClient.prompt(query)
				.advisors(
						a -> a.param(CONVERSATION_ID, conversationId)
				)
				.call().content();
	}

	@GetMapping("/messages")
	public List<Message> messages(@RequestParam(value = "conversation_id", defaultValue = "jinzi") String conversationId) {
		return messageWindowChatMemory.get(conversationId);
	}
}
