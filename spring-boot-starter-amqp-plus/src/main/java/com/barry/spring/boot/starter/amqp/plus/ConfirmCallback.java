package com.barry.spring.boot.starter.amqp.plus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author barry chen
 * @date 2023/5/14 17:47
 */
@Component
@Slf4j
public class ConfirmCallback implements RabbitTemplate.ConfirmCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(ack){
            return;
        }
        Message message =correlationData.getReturnedMessage();
        String messageStr = new String(message.getBody());
        String queue = message.getMessageProperties().getConsumerQueue();
        String exchange = message.getMessageProperties().getReceivedExchange();
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.error("===Message publish fail=== Exchange:{}, routingKey:{}, queue:{}, message:{} ", exchange, routingKey, queue, messageStr);
    }
}
