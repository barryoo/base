package com.barry.common.spring.config.feign;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>对 Feign 默认行为的扩展</p>
 *
 * @author chenpeng
 * Create time 2018年12月3日 下午1:38:25
 */
@Configuration
@ConditionalOnClass(Feign.class)
public class FeignExtensionAutoConfiguration {

    @Autowired
    private HttpMessageConverters messageConverters;

    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new FeignModuler.ResultDecoder());
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignModuler.ExceptionDecoder();
    }

    @Bean
    public Encoder feignFormEncoder() {
        ObjectFactory<HttpMessageConverters> httpMessageConvertsObjectFactory = () -> {
            return messageConverters;
        };
        return new SpringFormEncoder(new SpringEncoder(httpMessageConvertsObjectFactory));
    }
}
