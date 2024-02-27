package com.barry.common.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author barrychen
 * <p>
 * 对jackson ObjectMapper的扩展.
 * 可以传入你自定义的ObjectMapper,来进行增强.如果不传入ObjectMapper, 则使用默认的ObjectMapper.
 * <p>
 * 使用方式如下:
 * <p>
 * ObjectMapper ObjectMapper = new ObjectMapper();
 * ...
 * ...对objectMapper进行自定义配置
 * ...
 * JsonMapper jsonMapper = new JsonMapper(objectMapper);
 * jsonMapper.toJson(new Object());
 */
@Slf4j
public final class JsonMapper extends ObjectMapper {

    public static final String DEFAULT_DATE_SERIALIZER_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    public static final String DEFAULT_LOCAL_DATE_TIME_SERIALIZER_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_LOCAL_DATE_SERIALIZER_PATTERN = "yyyy-MM-dd";

    public static final String DEFAULT_LOCAL_DATE_TIME_DESERIALIZER_PATTERN = "[yyyy/MM/dd HH[:mm][:ss][.SSS]]"
            + "[yyyy-MM-dd HH[:mm][:ss][.SSS]][yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]]";
    public static final String DEFAULT_LOCAL_DATE_DESERIALIZER_PATTERN = "[yyyy/MM/dd]"
            + "[yyyy-MM-dd]";

    public JsonMapper() {
        super();
        //设置时间格式
        this.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_SERIALIZER_PATTERN));
        //允许单引号
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //允许无引号
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //允许在json中有,java类中没有的字段
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //注册时间反序列化
        SimpleModule module = new SimpleModule();

        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_TIME_SERIALIZER_PATTERN)));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_SERIALIZER_PATTERN)));

        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_TIME_DESERIALIZER_PATTERN).withZone(ZoneOffset.UTC)));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(
                DateTimeFormatter.ofPattern(DEFAULT_LOCAL_DATE_DESERIALIZER_PATTERN).withZone(ZoneOffset.UTC)));
        module.addDeserializer(Date.class, new JacksonComponent.DateJsonDeserializer());
        this.registerModule(module);
    }

    public JsonMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public String toJson(Object object) {
        try {
            return this.writeValueAsString(object);
        } catch (IOException e) {
            log.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如 \List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType) @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return this.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调用本函数.
     *
     * @see #createCollectionType(Class, Class...)
     */
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return (T) this.readValue(jsonString, javaType);
        } catch (IOException e) {
            log.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    public <T> T fromJson(String jsonString, TypeReference<T> valueTypeRef) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return this.readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            log.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 構造泛型的Collection Type如:
     * ArrayList<MyBean>, 则调用constructCollectionType(ArrayList.class,MyBean.class)
     * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 當JSON裡只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
     */
    public <T> T update(String jsonString, T object) {
        try {
            return (T) this.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            log.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }

    /**
     * 輸出JSONP格式數據.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

}
