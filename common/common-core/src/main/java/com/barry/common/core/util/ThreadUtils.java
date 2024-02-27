package com.barry.common.core.util;

import com.barry.common.core.exception.RetryExceededLimitException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BooleanSupplier;

/**
 * @author barry chen
 * @date 2020/11/27 4:20 下午
 */
public final class ThreadUtils extends org.apache.commons.lang3.ThreadUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadUtils.class);

    private ThreadUtils() {
    }

    public static void sleep(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.error("thread sleep error {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * sleep 直到condition为TRUE, 执行runnable. 如果超过maxRetry次数,则抛出异常
     *
     * @param condition
     * @param millis
     * @param maxRetry
     * @param runnable
     */
    public static void sleepUntil(BooleanSupplier condition, Long millis, Integer maxRetry, Runnable runnable) {
        int retry = 0;
        while (!condition.getAsBoolean()) {
            if (retry >= maxRetry) {
                throw new RetryExceededLimitException(String.format("sleep %s millis %s times, but condition is still false", millis, maxRetry));
            }
            sleep(millis);
            retry++;
        }
        runnable.run();
    }

}
