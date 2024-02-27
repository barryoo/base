package com.barry.common.mvc.config.multipartFile;

import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@EnableConfigurationProperties(MultipartProperties.class)
public class CommonsMultipartResolverConfiguration {
    @Bean
    public CommonsMultipartResolver commonsMultipartResolver(MultipartProperties multipartProperties) {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(Long.valueOf(multipartProperties.getFileSizeThreshold().toBytes()).intValue());
        resolver.setMaxUploadSize(Long.valueOf(multipartProperties.getMaxRequestSize().toBytes()).intValue());
        resolver.setMaxUploadSizePerFile(Long.valueOf(multipartProperties.getMaxFileSize().toBytes()).intValue());
        return resolver;
    }
}
