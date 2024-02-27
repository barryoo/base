package com.barry.common.mvc.config.convert;

import com.barry.common.core.bean.ApiResult;
import com.barry.common.core.util.JacksonComponent;
import com.barry.common.core.util.JsonMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * @author chen
 */
@Slf4j
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    public ObjectMapper objectMapper;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new BaseEnumConverterFactory());
        registry.addConverter(new StringDateConverter());
    }

    @Bean
    public ObjectToStringHttpMessageConverter objectToStringHttpMessageConverter() {
        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringDateConverter());
        conversionService.addConverter(new DateStringConvert());
        ObjectToStringHttpMessageConverter httpMessageConverter = new ObjectToStringHttpMessageConverter(conversionService);
        return httpMessageConverter;
    }

    /**
     * 该converter仅用于处理返回值为ApiResult的接口
     * - ObjectMapper的序列化策略为ALWAYS, 字段总是会被序列化, 即使为NULL或""
     * - 字符串为NULL时,会被序列化为""
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonAlways2HttpMessageConverter() {
        ObjectMapper jsonMapperForWeb = new JsonMapper(objectMapper);
        jsonMapperForWeb.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //注册null序列化
        jsonMapperForWeb.setSerializerFactory(jsonMapperForWeb.getSerializerFactory().withSerializerModifier(new JacksonComponent.NullJsonSerializerModifier()));
        return new MappingJackson2HttpMessageConverter(jsonMapperForWeb) {
            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                boolean support = Objects.equals(clazz, ApiResult.class);
                log.debug("mappingJacksonAlways2HttpMessageConverter {} supports {}", support ? "" : "don't", clazz);
                return support && super.canWrite(clazz, mediaType);
            }
        };
    }

    /**
     * 该converter用于处理返回值不是ApiResult的接口,
     * - 序列化策略为NON_NULL, 字段为NULL时,不会被序列化.
     *
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonNonNull2HttpMessageConverter() {
        ObjectMapper jsonMapperForRpc = new JsonMapper(objectMapper);
        jsonMapperForRpc.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return new MappingJackson2HttpMessageConverter(jsonMapperForRpc) {
            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                boolean support = !Objects.equals(clazz, ApiResult.class);
                log.debug("mappingJacksonNonNull2HttpMessageConverter {} supports {}", support ? "" : "don't", clazz);
                return support && super.canWrite(clazz, mediaType);
            }
        };
    }

}
