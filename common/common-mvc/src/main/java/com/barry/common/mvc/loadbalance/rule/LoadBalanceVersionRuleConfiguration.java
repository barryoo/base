package com.barry.common.mvc.loadbalance.rule;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;

/**
 * @author barry chen
 * @date 2022/12/16 15:48
 */
public class LoadBalanceVersionRuleConfiguration {

    @Bean
    IRule versionRule(){
        return new VersionRule();
    }

}
