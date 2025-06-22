package com.yohan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yohan
 * @Date 2025/06/22
 */
@RestController
@RequestMapping("/redis/rag")
@Slf4j
public class RedisRagController {
    private final RedisVectorStore redisVectorStore;

    @Autowired
    public RedisRagController(@Qualifier("redisVectorStoreCustom") RedisVectorStore redisVectorStore) {
        this.redisVectorStore = redisVectorStore;
    }

    @GetMapping("/import")
    public void importData() {
        log.info("start import data");

        HashMap<String, Object> map = new HashMap<>();
        map.put("year", "2025");
        map.put("name", "yohan");
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("可以核验的运营调价动作：\n" +
                        "1. 多因素调价\n" +
                        "2. 商圈调价\n" +
                        "3. 车型响应率调整\n" +
                        "4. 基础价调整"));
        documents.add(new Document("规则核验工具技术支持：\n" +
                        "1. yinhou.liu\n" +
                        "2. si.li\n" +
                        "3. san.zhang", Map.of("year", 2024)));

        documents.add(new Document("规则核验工具支持如下规则核验：\n" +
                "1. 基础车型定价配置\n" +
                "2. 搬家业务服务费配置\n" +
                "3. 用户出价配置\n" +
                "4. 特快配置\n" +
                "5. 到付附加费配置", map));
        redisVectorStore.add(documents);
    }

    @GetMapping("/search")
    public List<Document> search() {
        log.info("start search data");
        return redisVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("核验")
                .topK(2)
                .build());
    }

    @GetMapping("delete-filter")
    public void searchFilter() {
        log.info("start delete data with filter");
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        Filter.Expression expression = b.eq("name", "yohan").build();

        redisVectorStore.delete(expression);
    }
}