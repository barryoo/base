package com.barry.common.mvc.loadbalance.rule;

import com.barry.auto.transmitter.core.HolderContext;
import com.google.common.collect.Lists;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * 在local和dev环境, 默认启用VersionRule, 所有服务都会通过VersionRule进行负载均衡控制.
 * 如果不想启用, 可以使用 @ComponentScan.exclude排除该类.
 * 如果想要独立控制, 可以使用 @ComponentScan.exclude 排除该类. 然后在配置文件中,单独为服务指定负载均衡规则.
 *
 * @author barry chen
 * @date 2022/12/16 17:26
 */
@Profile({"dev","local"})
@RibbonClients(defaultConfiguration = {LoadBalanceVersionRuleConfiguration.class})
@Configuration
public class LoadBalanceVersionRuleAutoConfiguration {

    /**
     * 把VersionHolder放入HolderContext进行初始化.
     */
    @PostConstruct
    public void initAutoTransmitter() {
        HolderContext.config(Lists.newArrayList(VersionHolder.class));
    }
}
