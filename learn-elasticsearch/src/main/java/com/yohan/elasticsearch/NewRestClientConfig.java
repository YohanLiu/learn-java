package com.yohan.elasticsearch;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * {@see <a href="https://docs.spring.io/spring-data/elasticsearch/reference/migration-guides/migration-guide-4.4-5.0.html#custom-trace-level-logging">Elasticsearch Java High Level REST Client</a>}
 */
@Configuration
@Data
public class NewRestClientConfig extends ElasticsearchConfiguration {
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo("localhost:9200")
			.build();
	}
}