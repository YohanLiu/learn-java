package com.example.javabasic.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yohan
 * @Date 2024/02/01
 */
public interface ProductDao extends ElasticsearchRepository<Product, Long> {
}
