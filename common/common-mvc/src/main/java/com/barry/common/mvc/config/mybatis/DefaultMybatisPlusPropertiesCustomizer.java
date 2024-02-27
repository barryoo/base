package com.barry.common.mvc.config.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;

/**
 * 通过MybatisPlusPropertiesCustomizer, 加载自定义的DefaultMybatisPlusPropertiesCustomizer
 *
 * @author chen
 * @since 2020-07-22
 */
public class DefaultMybatisPlusPropertiesCustomizer implements MybatisPlusPropertiesCustomizer {
    @Override
    public void customize(MybatisPlusProperties properties) {
        DefaultMybatisConfiguration mybatisConfiguration = new DefaultMybatisConfiguration();
        properties.setConfiguration(mybatisConfiguration);
    }
}
