package com.barry.common.spring.config.ribbon;

import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author barry chen
 * @date 2022/12/9 23:10
 */
@Configuration
//@ConditionalOnProperty(value = "spring.cloud.loadbalancer.ribbon.enabled", matchIfMissing = true)
public class RibbonConfig {

    @Bean
    @Primary
    public LoadBalancedRetryFactory loadBalancedRetryPolicyFactory(SpringClientFactory clientFactory) {
        return new CustomLoadBalancedRetryFactory(clientFactory);
    }

}
