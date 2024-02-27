package com.barry.common.mvc.loadbalance.rule;

import com.barry.auto.transmitter.core.HolderContext;
import com.barry.common.core.enums.EnvEnum;
import com.barry.common.core.util.StringUtils;
import com.barry.common.spring.util.SpringEnvUtils;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class VersionRule extends RoundRobinRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionRule.class);

    @Autowired
    private EurekaInstanceConfigBean eurekaClientConfigBean;

    /**
     * 根据version选择服务
     *
     * @param lb
     * @param key
     * @return
     */
    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        // get server name
        String serverName = "";
        if(lb instanceof BaseLoadBalancer){
            serverName = ((BaseLoadBalancer) lb).getName();
        }else{
            serverName = Optional.ofNullable(lb.getAllServers()).filter(servers -> !servers.isEmpty()).map(servers -> servers.get(0).getMetaInfo().getAppName()).orElse("");
        }
        // get version
        String version = (String) HolderContext.get(VersionHolder.class);
        if (StringUtils.isEmpty(version)) {
            version = eurekaClientConfigBean.getMetadataMap().get(RibbonRuleConstant.HEADER_NAME);
        }
        log.debug("loadBalance server[{}] with version[{}]", serverName, version);

        // up server list, 并根据version过滤
        List<Server> targetList = null;
        List<Server> upList = lb.getReachableServers();
        if (StringUtils.isNotEmpty(version)) {
            //取指定版本号的实例
            String finalVersion = version;
            targetList = upList.stream().filter(
                    server -> finalVersion.equalsIgnoreCase(
                            ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata().get(RibbonRuleConstant.HEADER_NAME)
                    )
            ).collect(Collectors.toList());
        }

        //如果上一步没有找到, 则使用当前服务的运行环境, 找到对应的服务.
        if (targetList == null || targetList.size() == 0) {
            //取指定版本号的实例
            String env = SpringEnvUtils.getProfile();
            if(StringUtils.isBlank(env) || SpringEnvUtils.isDev() || SpringEnvUtils.isLocal()){
                env = EnvEnum.DEV.getName();
            }
            String finalVersion = env;
            targetList = upList.stream().filter(
                    server -> finalVersion.equalsIgnoreCase(
                            ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata().get(RibbonRuleConstant.HEADER_NAME)
                    )
            ).collect(Collectors.toList());
        }

        //如果上一步没有找到, 则选择没有设置version的服务
        if (targetList == null || targetList.size() == 0) {
            //只取无版本号的实例
            targetList = upList.stream().filter(
                    server -> {
                        String metadataVersion = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata().get(RibbonRuleConstant.HEADER_NAME);
                        return StringUtils.isEmpty(metadataVersion);
                    }
            ).collect(Collectors.toList());
        }

        if (targetList.size() > 0) {
            return getServer(targetList);
        }

        //如果上一步没有找到, 则使用RoundRobinRule的默认规则 选择服务
        return super.choose(lb, key);
    }

    /**
     * 随机取一个实例
     */
    private Server getServer(List<Server> upList) {
        int nextInt = RandomUtils.nextInt(upList.size());
        Server server = upList.get(nextInt);
        LOGGER.info("serverId: " + server.toString());
        return server;
    }
}
