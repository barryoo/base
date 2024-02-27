package com.barry.common.spring.config.id;

import com.barry.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DistributedIdWorkerIdGenerator {

    private static final String ID_WORKER_ID_INCREMENT_KEY = "ID_WORKER_ID_INCREMENT";

    private static final int MAX_WORKER_ID = 64;

    private static final int EXPIRE_SECONDS = 3600;

    private static final String ID_WORKER_ID_PREFIX = "ID_WORKER_ID_";

    /**
     * 生成workerId后由服务自己管理
     *
     * @return workerId
     */
    Long acquireId(RedisTemplate<String, Object> redisTemplate) {
        //通过服务名获取自增器的当前值
        RedisAtomicLong redisAtomicLong = new RedisAtomicLong(ID_WORKER_ID_INCREMENT_KEY, redisTemplate.getConnectionFactory());
        //循环检查workerId是否已经被占用，如果没被占用则设定一个锁
        Long incrementIndex;
        Long workerId;
        for (int i = 0; i < MAX_WORKER_ID; i++) {
            incrementIndex = redisAtomicLong.getAndIncrement();
            workerId = incrementIndex % MAX_WORKER_ID;
            if (redisTemplate.opsForValue().setIfAbsent(ID_WORKER_ID_PREFIX + workerId, workerId, EXPIRE_SECONDS, TimeUnit.SECONDS)) {
                return workerId;
            }
        }
        log.error("Snowflake id run out.");
        throw new BusinessException("Snowflake id run out.");
    }

    /**
     * 服务实例需要维护定时任务来访问redis维护workerId续签
     *
     * @param workerId workerId
     */
    void renewalId(RedisTemplate<String, Object> redisTemplate, Long workerId) {
        redisTemplate.expire(ID_WORKER_ID_PREFIX + workerId, 3600, TimeUnit.SECONDS);
    }

}
