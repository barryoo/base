package com.barry.xxl.job.spring.boot.starter.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Xxl job 自动配置</p>
 *
 * @author chenpeng
 * Create at January 24, 2019 at 11:39:52 GMT+8
 */
@Data
@ConfigurationProperties(prefix = "xxl-job")
public class XxlJobConfigProperties {

    private Admin    admin;
    private Executor executor;

    /**
     * Admin
     */
    @Data
    public static class Admin {
        private String address;
    }

    /**
     * Executor
     */
    @Data
    public static class Executor {
        private String appName;
        private String ip;
        private int port;
        private String accessToken;
        private String logPath;
        private int logRetentionDays;
    }
}
