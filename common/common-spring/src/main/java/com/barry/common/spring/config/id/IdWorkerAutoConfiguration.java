package com.barry.common.spring.config.id;

import com.barry.common.core.util.id.IdWorkerContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class IdWorkerAutoConfiguration {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final DistributedIdWorkerIdGenerator DISTRIBUTED_ID_WORKER_ID_GENERATOR = new DistributedIdWorkerIdGenerator();
    private Long workerId;

    @Bean
    @Primary
    public IdWorkerContainer idWorkerContainer() {
        workerId = DISTRIBUTED_ID_WORKER_ID_GENERATOR.acquireId(redisTemplate);
        log.info("IdWorkerContainer init, workerId: {}", workerId);
        return new IdWorkerContainer(workerId);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void renewalId() {
        DISTRIBUTED_ID_WORKER_ID_GENERATOR.renewalId(redisTemplate, workerId);
    }
}
