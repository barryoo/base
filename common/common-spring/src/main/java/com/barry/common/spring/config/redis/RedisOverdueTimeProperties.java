package com.barry.common.spring.config.redis;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.cache.redis")
@Data
public class RedisOverdueTimeProperties {

    private Boolean enabled;
    private Map<String, Duration> expires = Maps.newHashMap();
}
