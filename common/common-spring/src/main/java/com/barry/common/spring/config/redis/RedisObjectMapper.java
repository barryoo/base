package com.barry.common.spring.config.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * <p>Redis 专用的 ObjectMapper </p>
 *
 * @author chenpeng
 * Create at February 25, 2019 at 19:52:21 GMT+8
 */
public class RedisObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -696530460999764926L;

    public RedisObjectMapper() {
        super();
        // 去掉各种@JsonSerialize注解的解析
        this.configure(MapperFeature.USE_ANNOTATIONS, false);
        // 只针对非空的值进行序列化
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 将类型序列化到属性json字符串中
        this.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 对于找不到匹配属性的时候忽略报错
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 不包含任何属性的bean也不报错
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 可以包含转移符等字符
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许单引号
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
}
