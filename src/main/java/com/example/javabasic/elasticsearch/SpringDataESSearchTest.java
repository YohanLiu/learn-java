package com.example.javabasic.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataESSearchTest {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * term 查询.
     * <p>term查询在keyword字段和text字段的区别，keyword字段是将整个字段信息作为索引，term查询需要查询条件与keyword内容完全匹配才能查询到。
     * <p>text字段会进行分词建立索引，term查询时，只要term查询条件能够匹配分词后建立的索引即可查询到
     */
    @Test
    public void termQuery() {
        //TermQuery termQuery = Queries.termQuery("category", "手机"); // keyword字段
        TermQuery termQuery = Queries.termQuery("title", "华"); // text字段
        Query query = new Query.Builder().term(termQuery).build();
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).build();

        SearchHits<Product> searchHits = elasticsearchTemplate.search(nativeQuery, Product.class);
        searchHits.getSearchHits()
                .forEach(searchHit -> System.out.println(searchHit.getContent()));
    }

    /**
     * term 查询加分页
     */
    @Test
    public void termQueryByPage() {
        int currentPage = 0;
        int pageSize = 5;
        //设置查询分页
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize);
        TermQuery termQuery = Queries.termQuery("category", "手机");
        Query query = new Query.Builder().term(termQuery).build();
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withPageable(pageRequest).build();

        SearchHits<Product> searchHits = elasticsearchTemplate.search(nativeQuery, Product.class);
        searchHits.getSearchHits()
                .forEach(searchHit -> System.out.println(searchHit.getContent()));
    }
}