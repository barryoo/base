package com.barry.common.mvc.config.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chen
 * @since 2020-07-22
 */
@Data
@ConfigurationProperties("mybatis.refresh")
public class MybatisMapperRefreshProperties {

    /**
     * 是否启用刷新
     */
    private boolean enabled = false;
    /**
     * 延迟加载时间
     */
    private int delaySeconds = 10;
    /**
     * 刷新间隔时间
     */
    private int sleepSeconds = 20;

}
