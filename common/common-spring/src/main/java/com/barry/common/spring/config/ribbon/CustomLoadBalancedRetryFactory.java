package com.barry.common.spring.config.ribbon;

import com.google.common.collect.Lists;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.BackOffPolicy;

import java.net.ConnectException;
import java.util.List;

/**
 * 修改默认的重试策略. 只有当发生连接超时异常时,才会进行重试, 其他情况不重试. 避免ReadTimeout重试引发性能灾难.
 * 1. 连接超时: 重试sameServer1次, 然后重试nextServer2次
 * 2. 读取超时: 完全不重试.
 * 该类在 RibbonLoadBalancedRetryFactory 的基础上修改.
 * 由于默认重试策略 RibbonLoadBalancedRetryPolicy 是通过 RequestSpecificRetryHandler 来判断异常是否可以重试的, 而retryHandler无法进行继承或修改.
 * 所以只能通过自定义重试策略来实现.
 *
 * @author barry chen
 * @date 2022/12/9 18:00
 */
public class CustomLoadBalancedRetryFactory implements LoadBalancedRetryFactory {

    private SpringClientFactory clientFactory;
    private final List<Class<? extends Throwable>> retriable = Lists.<Class<? extends Throwable>>newArrayList(ConnectException.class);

    public CustomLoadBalancedRetryFactory(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public LoadBalancedRetryPolicy createRetryPolicy(String service,
            ServiceInstanceChooser serviceInstanceChooser) {
        RibbonLoadBalancerContext lbContext = this.clientFactory.getLoadBalancerContext(service);
        return new CustomLoadBalancedRetryPolicy(service, lbContext, serviceInstanceChooser, clientFactory.getClientConfig(service));
    }

    @Override
    public RetryListener[] createRetryListeners(String service) {
        return new RetryListener[0];
    }

    @Override
    public BackOffPolicy createBackOffPolicy(String service) {
        return null;
    }

}
