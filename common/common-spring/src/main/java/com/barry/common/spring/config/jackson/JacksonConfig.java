package com.barry.common.spring.config.jackson;

import com.barry.common.core.util.JacksonComponent;
import com.barry.common.core.util.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class JacksonConfig {

    @Value("${spring.jackson.local-date-time-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
                jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(JsonMapper.DEFAULT_LOCAL_DATE_TIME_SERIALIZER_PATTERN)));
                jacksonObjectMapperBuilder.serializerByType(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(JsonMapper.DEFAULT_LOCAL_DATE_SERIALIZER_PATTERN)));

                jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(
                        DateTimeFormatter.ofPattern(JsonMapper.DEFAULT_LOCAL_DATE_TIME_DESERIALIZER_PATTERN + pattern)
                                .withZone(ZoneOffset.UTC)));
                jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(
                        DateTimeFormatter.ofPattern(JsonMapper.DEFAULT_LOCAL_DATE_DESERIALIZER_PATTERN)
                                .withZone(ZoneOffset.UTC)));
                jacksonObjectMapperBuilder.deserializerByType(Date.class, new JacksonComponent.DateJsonDeserializer());
            }
        };
    }

}
