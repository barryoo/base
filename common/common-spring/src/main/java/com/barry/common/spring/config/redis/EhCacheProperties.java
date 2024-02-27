package com.barry.common.spring.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author barry chen
 * @date 2023/7/14 15:48
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.cache.ehcache")
public class EhCacheProperties {
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }
}
