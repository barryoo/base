package com.barry.common.mvc.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author chen
 * @since 2020-07-22
 */
@Configuration
@AutoConfigureAfter(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties(MybatisMapperRefreshProperties.class)
@Import(DefaultMybatisPlusPropertiesCustomizer.class)
public class MybatisMapperRefreshAutoConfiguration {
    private final MybatisMapperRefreshProperties mybatisMapperRefreshProperties;
    private final MybatisPlusProperties properties;
    private final SqlSessionFactory sqlSessionFactory;

    public MybatisMapperRefreshAutoConfiguration(
            MybatisMapperRefreshProperties mybatisMapperRefreshProperties,
            MybatisPlusProperties properties, SqlSessionFactory sqlSessionFactory) {
        this.mybatisMapperRefreshProperties = mybatisMapperRefreshProperties;
        this.properties = properties;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Bean
    public MybatisMapperRefresh enableRefresh() {
        return new MybatisMapperRefresh(properties.resolveMapperLocations(), sqlSessionFactory,
                mybatisMapperRefreshProperties.getDelaySeconds(),
                mybatisMapperRefreshProperties.getSleepSeconds(),
                mybatisMapperRefreshProperties.isEnabled());
    }
}
