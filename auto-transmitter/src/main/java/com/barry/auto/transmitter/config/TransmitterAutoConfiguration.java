package com.barry.auto.transmitter.config;

import com.barry.auto.transmitter.interceptor.TransmitterFeignClientInterceptor;
import com.barry.auto.transmitter.interceptor.TransmitterHttpRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnProperty(name = "spring.transmitter.enable", havingValue = "true")
@Configuration
@EnableConfigurationProperties(TransmitterProperties.class)
@Import({TransmitterHttpRequestInterceptor.class, TransmitterFeignClientInterceptor.class})
public class TransmitterAutoConfiguration {

    private final TransmitterProperties transmitterProperties;

    public TransmitterAutoConfiguration(
            TransmitterProperties transmitterProperties) {
        this.transmitterProperties = transmitterProperties;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(TransmitterHttpRequestInterceptor transmitterHttpRequestInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(transmitterHttpRequestInterceptor).addPathPatterns("/**");
            }
        };
    }
}
