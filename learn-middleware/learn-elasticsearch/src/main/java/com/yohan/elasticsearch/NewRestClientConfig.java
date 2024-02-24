package com.yohan.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;


@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
@Data
public class NewRestClientConfig extends ElasticsearchConfiguration {
	private String host ;

	private Integer port ;
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo(host + ":" + port)
			.build();
	}
}