package com.barry.auto.transmitter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.transmitter")
public class TransmitterProperties {
    private boolean enable;
}
