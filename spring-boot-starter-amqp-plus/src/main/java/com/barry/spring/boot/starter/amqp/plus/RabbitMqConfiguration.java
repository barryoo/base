package com.barry.spring.boot.starter.amqp.plus;

import com.barry.common.core.util.JsonMapper;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;

/**
 * @author chen
 */
@Configuration
public class RabbitMqConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        MyJackson2JsonMessageConverter messageConverter =
                new MyJackson2JsonMessageConverter(new JsonMapper(), MimeTypeUtils.parseMimeType(MessageProperties.CONTENT_TYPE_JSON), "*");
        messageConverter.setClassMapper(classMapper());
        return messageConverter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        return classMapper;
    }
}
