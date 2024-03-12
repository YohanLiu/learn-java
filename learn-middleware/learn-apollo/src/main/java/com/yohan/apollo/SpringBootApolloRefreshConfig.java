package com.yohan.apollo;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.cloud.context.scope.refresh.RefreshScope;

@ConditionalOnProperty("redis.cache.enabled")
@Component
@Slf4j
public class SpringBootApolloRefreshConfig {

    private final SampleRedisConfig sampleRedisConfig;
    private final RefreshScope refreshScope;

    public SpringBootApolloRefreshConfig(
            final SampleRedisConfig sampleRedisConfig,
            final RefreshScope refreshScope) {
        this.sampleRedisConfig = sampleRedisConfig;
        this.refreshScope = refreshScope;
    }

    @ApolloConfigChangeListener(value = "${listeners}", interestedKeyPrefixes = {"redis.cache."})
    public void onChange(ConfigChangeEvent changeEvent) {
        log.info("before refresh {}", sampleRedisConfig.toString());
        refreshScope.refresh("sampleRedisConfig");
        log.info("after refresh {}", sampleRedisConfig);
    }
}