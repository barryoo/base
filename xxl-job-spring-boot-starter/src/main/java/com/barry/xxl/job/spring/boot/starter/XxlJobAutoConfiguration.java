package com.barry.xxl.job.spring.boot.starter;

import com.barry.xxl.job.spring.boot.starter.bean.XxlJobConfigProperties;
import com.xxl.job.core.log.XxlJobFileAppender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>Xxl job 统一配置</p>
 *
 * @author chenpeng
 * Create at January 24, 2019 at 11:35:58 GMT+8
 */
@Configuration
@ConditionalOnProperty({
        "xxl-job.executor.app-name",
        "xxl-job.executor.port"
})
@EnableConfigurationProperties(XxlJobConfigProperties.class)
public class XxlJobAutoConfiguration {

    private final XxlJobConfigProperties config;

    public XxlJobAutoConfiguration(XxlJobConfigProperties config) {
        this.config = config;
    }

    /**
     * init xxl job spring executor bean
     *
     * @return xxl job spring executor bean
     */
    @Primary
    @Bean
    public CustomExecutor xxlJobSpringExecutor(ApplicationContext context) {
        CustomExecutor executor = new CustomExecutor();
        executor.setAdminAddresses(config.getAdmin().getAddress());
        executor.setAppname(config.getExecutor().getAppName());
        executor.setIp(config.getExecutor().getIp());
        executor.setPort(config.getExecutor().getPort());
        executor.setAccessToken(config.getExecutor().getAccessToken());
        executor.setLogPath(config.getExecutor().getLogPath());
        executor.setLogRetentionDays(config.getExecutor().getLogRetentionDays());
        executor.setApplicationContext(context);
        XxlJobFileAppender.initLogPath(config.getExecutor().getLogPath());
        return executor;
    }
}
