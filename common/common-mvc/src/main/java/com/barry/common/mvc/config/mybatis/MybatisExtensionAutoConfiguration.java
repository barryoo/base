package com.barry.common.mvc.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>扩展 Mybatis-plus 配置</p>
 *
 * @author chenpeng
 * Create time Dec 13, 2018 8:14:48 PM
 */
@Configuration
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
@Import(DefaultMapperScannerRegistrar.class)
public class MybatisExtensionAutoConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setLimit(-1);
        return paginationInterceptor;
    }
}
