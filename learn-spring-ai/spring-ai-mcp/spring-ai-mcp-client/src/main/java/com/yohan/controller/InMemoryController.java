package com.yohan.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * 基于内存的记忆聊天.
 * 
 * @author yohan
 * @Date 2025/06/16
 */
@RestController
@RequestMapping("/advisor/memory/in")
public class InMemoryController {

    private final ChatClient chatClient;
    private final InMemoryChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
    private final int MAX_MESSAGES = 100;
    private final MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(MAX_MESSAGES)
            .build();

    private static final String DEFAULT_PROMPT = "你是一个规则核验助手，根据用户提供的飞书链接去和数据库中的规则进行核对！";

    public InMemoryController(ChatClient.Builder builder, ToolCallbackProvider tools) {
        this.chatClient = builder
                .defaultToolCallbacks(tools)
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
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
