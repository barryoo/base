package com.barry.common.mvc.config;

import com.barry.common.core.util.StopWatchRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * @author barry chen
 * @date 2022/10/26 12:03
 */
@Configuration
public class StopWatchRecorderConfiguration {

    @RequestScope
    @Bean
    StopWatchRecorder stopWatchRecorder(){
        return new StopWatchRecorder();
    }
}
