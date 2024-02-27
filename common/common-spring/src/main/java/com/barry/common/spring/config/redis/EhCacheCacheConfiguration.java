package com.barry.common.spring.config.redis;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@ConditionalOnProperty(prefix = "spring.cache.ehcache", value = {"enabled"}, havingValue = "true")
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class EhCacheCacheConfiguration {
    /**
     * ehCache
     */
    public static final String EHCACHE_CACHE_MAANGER = "ehCacheCacheManager";


    private final CacheProperties cacheProperties;

    EhCacheCacheConfiguration(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    /**
     * 创建ehCacheCacheManager
     */
    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {

        Resource p = this.cacheProperties.getEhcache().getConfig();
        Resource location = this.cacheProperties
                .resolveConfigLocation(p);
        return new EhCacheCacheManager(net.sf.ehcache.CacheManager.create(EhCacheManagerUtils.parseConfiguration(location)));
    }
}
