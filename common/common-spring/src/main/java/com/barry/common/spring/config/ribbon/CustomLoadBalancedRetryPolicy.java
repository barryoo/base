/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barry.common.spring.config.ribbon;

import com.barry.common.core.util.StopWatchRecorder;
import com.google.common.collect.Lists;
import com.netflix.client.Utils;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.RibbonServer;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerContext;
import org.springframework.cloud.openfeign.ribbon.RetryableFeignLoadBalancer;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link LoadBalancedRetryPolicy} for Ribbon clients.
 * 代码从RibbonLoadBalancedRetryPolicy复制过来. 做了以下修改:
 * 1. 修改了canRetry方法, 用于自定义可以重试的异常类型.
 * 2. 为registerThrowable方法添加详细的INFO界别的日志.
 *
 * @author Ryan Baxter
 */
@Slf4j
public class CustomLoadBalancedRetryPolicy implements LoadBalancedRetryPolicy {

	/**
	 * Retrayable status codes config key.
	 */
	public static final IClientConfigKey<String> RETRYABLE_STATUS_CODES = new CommonClientConfigKey<String>(
			"retryableStatusCodes") {
	};

	private int sameServerCount = 0;

	private int nextServerCount = 0;

	private String serviceId;

	private RibbonLoadBalancerContext lbContext;

	private ServiceInstanceChooser loadBalanceChooser;

	List<Integer> retryableStatusCodes = new ArrayList<>();

	public CustomLoadBalancedRetryPolicy(String serviceId,
			RibbonLoadBalancerContext context,
			ServiceInstanceChooser loadBalanceChooser) {
		this.serviceId = serviceId;
		this.lbContext = context;
		this.loadBalanceChooser = loadBalanceChooser;
	}

	public CustomLoadBalancedRetryPolicy(String serviceId,
			RibbonLoadBalancerContext context, ServiceInstanceChooser loadBalanceChooser,
			IClientConfig clientConfig) {
		this.serviceId = serviceId;
		this.lbContext = context;
		this.loadBalanceChooser = loadBalanceChooser;
		String retryableStatusCodesProp = clientConfig
				.getPropertyAsString(RETRYABLE_STATUS_CODES, "");
		String[] retryableStatusCodesArray = retryableStatusCodesProp.split(",");
		for (String code : retryableStatusCodesArray) {
			if (!StringUtils.isEmpty(code)) {
				try {
					retryableStatusCodes.add(Integer.valueOf(code.trim()));
				}
				catch (NumberFormatException e) {
					log.warn("We cant add the status code because the code [ " + code
							+ " ] could not be converted to an integer. ", e);
				}
			}
		}
	}

	public boolean canRetry(LoadBalancedRetryContext context) {
        Throwable t = context.getLastThrowable();
        if (t == null) {
            return canRetryForHttpMethod(context);
        }
		return canRetryForException(t, context) && canRetryForHttpMethod(context);
	}

    private boolean canRetryForHttpMethod(LoadBalancedRetryContext context){
        HttpMethod method = context.getRequest().getMethod();
        return HttpMethod.GET == method || lbContext.isOkToRetryOnAllOperations();
    }

    private boolean canRetryForException(Throwable t, LoadBalancedRetryContext context) {
        return isConnectTimeoutException(t, context);
    }

    /**
     * 判断是否是连接超时
     * 1. 属于ConnectException或者其子类
     * 2. 属于SocketTimeoutException或者其子类 且 message中包含 connect timed out
     *
     * @param t
     * @return
     */
    private boolean isConnectTimeoutException(@Nonnull Throwable t, LoadBalancedRetryContext context) {
        if (Utils.isPresentAsCause(t, Lists.<Class<? extends Throwable>>newArrayList(ConnectException.class))) {
            log.info("ribbon retry: is ConnectException, will retry");
            return true;
        }
        if (Utils.isPresentAsCause(t, Lists.<Class<? extends Throwable>>newArrayList(SocketTimeoutException.class))
                && t.getMessage().contains("connect timed out")) {
            log.info("ribbon retry: is SocketTimeoutException and connect timed out, will retry");
            return true;
        }
        log.info("ribbon retry: exception is {},  will not retry", Optional.ofNullable(t.getCause()).orElse(t).getClass().getName());
        return false;
    }

    @Override
	public boolean canRetrySameServer(LoadBalancedRetryContext context) {
		return sameServerCount < lbContext.getRetryHandler().getMaxRetriesOnSameServer()
				&& canRetry(context);
	}

	@Override
	public boolean canRetryNextServer(LoadBalancedRetryContext context) {
		// this will be called after a failure occurs and we increment the counter
		// so we check that the count is less than or equals to too make sure
		// we try the next server the right number of times
		return nextServerCount <= lbContext.getRetryHandler().getMaxRetriesOnNextServer()
				&& canRetry(context);
	}

	@Override
	public void close(LoadBalancedRetryContext context) {

	}

	@Override
	public void registerThrowable(LoadBalancedRetryContext context, Throwable throwable) {
        StopWatchRecorder sw = new StopWatchRecorder();
        sw.split(String.format("serviceId:%s, URI:%s maxRetriesOnSameServer:%d, maxRetriesOnNextServer:%d",
                serviceId, context.getRequest().getURI(), lbContext.getRetryHandler().getMaxRetriesOnSameServer(), lbContext.getRetryHandler().getMaxRetriesOnNextServer()));
        sw.split(context.toString());
        ILoadBalancer lb = ((RetryableFeignLoadBalancer) this.loadBalanceChooser).getLoadBalancer();
        sw.split("serverList = " + lb.getAllServers().stream().map(Server::toString).collect(Collectors.joining("; ")));
		// if this is a circuit tripping exception then notify the load balancer
		if (lbContext.getRetryHandler().isCircuitTrippingException(throwable)) {
			updateServerInstanceStats(context);
		}
		// Check if we need to ask the load balancer for a new server.
		// Do this before we increment the counters because the first call to this method
		// is not a retry it is just an initial failure.
        Boolean canRetrySameServer = canRetrySameServer(context);
        Boolean canRetryNextServer = canRetryNextServer(context);
        sw.split("canRetrySameServer = " + canRetrySameServer + ", canRetryNextServer = " + canRetryNextServer);
		if (!canRetrySameServer && canRetryNextServer) {
            ServiceInstance serviceInstance = loadBalanceChooser.choose(serviceId);
            sw.split("choose next server = " + serviceInstance.toString());
			context.setServiceInstance(serviceInstance);
		}
		// This method is called regardless of whether we are retrying or making the first
		// request.
		// Since we do not count the initial request in the retry count we don't reset the
		// counter
		// until we actually equal the same server count limit. This will allow us to make
		// the initial
		// request plus the right number of retries.
		if (sameServerCount >= lbContext.getRetryHandler().getMaxRetriesOnSameServer()
				&& canRetry(context)) {
			// reset same server since we are moving to a new server
			sameServerCount = 0;
			nextServerCount++;
            sw.split("reset sameServerCount=0, nextServerCount = " + nextServerCount);
			if (!canRetryNextServer(context)) {
                sw.split("can't retry next server");
				context.setExhaustedOnly();
			}
		}
		else {
			sameServerCount++;
            sw.split("sameServerCount++, now it's " + sameServerCount);
		}
        log.info(sw.stopAndOutput(""));
	}

	private void updateServerInstanceStats(LoadBalancedRetryContext context) {
		ServiceInstance serviceInstance = context.getServiceInstance();
		if (serviceInstance instanceof RibbonServer) {
			Server lbServer = ((RibbonServer) serviceInstance).getServer();
			ServerStats serverStats = lbContext.getServerStats(lbServer);
			serverStats.incrementSuccessiveConnectionFailureCount();
			serverStats.addToFailureCount();
			log.debug(lbServer.getHostPort() + " RetryCount: "
					+ context.getRetryCount() + " Successive Failures: "
					+ serverStats.getSuccessiveConnectionFailureCount()
					+ " CircuitBreakerTripped:" + serverStats.isCircuitBreakerTripped());
		}
	}

	@Override
	public boolean retryableStatusCode(int statusCode) {
		return retryableStatusCodes.contains(statusCode);
	}

}
