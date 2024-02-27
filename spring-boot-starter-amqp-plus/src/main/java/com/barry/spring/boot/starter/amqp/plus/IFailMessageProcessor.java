package com.barry.spring.boot.starter.amqp.plus;

import org.springframework.amqp.core.Message;

/**
 * 失败消息处理
 *
 * @author barry chen
 * @date 2021/3/12 17:25
 */
public interface IFailMessageProcessor {

    /**
     * 处理消费失败的消息
     *
     * @param message
     * @param ex
     */
    void process(Message message, Exception ex);
}
