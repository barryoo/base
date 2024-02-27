package com.barry.common.spring.util;

import com.barry.common.core.util.JsonMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通用的Json处理工具.
 * 注入了spring.jackson的objectMapper, 使得全局使用同一规则进行json处理.
 * <p>
 * 如果有特殊需要,对json处理, 可以使用 {@link JsonMapper},传入自定义{@link ObjectMapper}
 */
@Component
public class JsonUtils {
    private static JsonMapper jsonMapper;

    public static JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    public static String toJson(Object object) {
        return jsonMapper.toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return jsonMapper.fromJson(jsonString, clazz);
    }

    public static <T> T fromJson(String jsonString, JavaType javaType) {
        return jsonMapper.fromJson(jsonString, javaType);
    }

    public static <T> T fromJson(String jsonString, TypeReference<T> valueTypeRef) {
        return jsonMapper.fromJson(jsonString, valueTypeRef);
    }

    public static String toJsonP(String functionName, Object object) {
        return jsonMapper.toJsonP(functionName, object);
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        if (objectMapper != null) {
            JsonUtils.jsonMapper = new JsonMapper(objectMapper);
        } else {
            JsonUtils.jsonMapper = new JsonMapper();
        }
        //如果字段为NULL,则不序列化
        JsonUtils.jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
