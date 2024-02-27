package com.barry.common.spring.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * <p>Redis 自动配置</p>
 *
 * @author chenpeng
 * Create at February 18, 2019 at 15:58:59 GMT+8
 */
@EnableCaching
@ConditionalOnProperty(prefix = "spring.cache.redis", value = {"enabled"}, havingValue = "true")
@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class RedisExtensionAutoConfiguration {

    /**
     * redis
     */
    public static final String REDIS_CACHE_MANAGER = "redisCacheManager";


    @Autowired
    private RedisOverdueTimeProperties redisProperties;

    /**
     * <p>配置 RedisTemplate</p>
     *
     * @author chenpeng
     * Create at February 18, 2019 at 15:59:19 GMT+8
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new RedisObjectMapper();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.setValueSerializer(valueSerializer(objectMapper));
        redisTemplate.setHashValueSerializer(valueSerializer(objectMapper));
        redisTemplate.setEnableTransactionSupport(false);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * <p>配置缓存</p>
     *
     * @author chenpeng
     * Create at February 18, 2019 at 15:59:46 GMT+8
     */
    @Bean(REDIS_CACHE_MANAGER)
    @Primary
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
            CacheProperties cacheProperties) {

        ObjectMapper objectMapper = new RedisObjectMapper();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        if (cacheProperties != null && cacheProperties.getRedis() != null) {
            Duration timeToLive = cacheProperties.getRedis().getTimeToLive();
            if (timeToLive != null) {
                config = config.entryTtl(timeToLive);
            }
        }

        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        valueSerializer(objectMapper)));

        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        ImmutableSet.Builder<String> cacheNames = ImmutableSet.builder();
        ImmutableMap.Builder<String, RedisCacheConfiguration> cacheConfig = ImmutableMap.builder();
        for (Map.Entry<String, Duration> entry : redisProperties.getExpires().entrySet()) {
            cacheNames.add(entry.getKey());
            cacheConfig.put(entry.getKey(), config.entryTtl(entry.getValue()));
        }

        return RedisCacheManager.builder(redisCacheWriter)
                .cacheDefaults(config)
                .initialCacheNames(cacheNames.build())
                .withInitialCacheConfigurations(cacheConfig.build())
                .build();
    }

    @Bean
    public RedisHelper redisHelper(RedisTemplate<String, Object> redisTemplate) {
        return new RedisHelper(redisTemplate);
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer(ObjectMapper mapper) {
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
