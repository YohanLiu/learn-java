package com.yohan.elasticsearch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataESIndexTest {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    //创建索引并增加映射配置
    @Test
    public void createIndex() {
        // 创建索引
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(Product.class);
        if (!indexOperations.exists()) { // 当前索引不存在
            //boolean result1 =indexOperations.create();// 只是创建索引 。mappings没有映射
            boolean result2 = indexOperations.putMapping();// 映射属性
            System.out.println("创建结果：" + ",映射结果：" + result2);
        } else {
            System.out.println("文档已存在");
        }
    }

    @Test
    public void deleteIndex() {
        // 删除索引
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(Product.class);
        boolean delete = indexOperations.delete();   // 删除索引
        System.out.println("删除索引："+delete);
    }

}