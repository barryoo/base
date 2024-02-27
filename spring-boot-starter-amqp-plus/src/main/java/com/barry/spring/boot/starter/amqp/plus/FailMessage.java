package com.barry.spring.boot.starter.amqp.plus;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.amqp.core.Message;

import java.io.Serializable;
import java.util.Date;

/**
 * @author barry chen
 * @date 2021/3/12 17:53
 */
@Data
@Accessors(chain = true)
public class FailMessage implements Serializable {
    /**
     * UUID
     */
    private String id;
    /**
     * 原消息id
     */
    private String messageId;
    /**
     * 原exchange
     */
    private String oriExchange;
    /**
     * 原route key
     */
    private String oriRouteKey;
    /**
     * 原队列
     */
    private String oriQueue;
    /**
     * 消息body
     */
    private String messageBody;
    /**
     * 消息property
     */
    private String messageProperties;
    /**
     * 原消息对象序列化 byte[]
     */
    private byte[] messageBinary;
    /**
     * 原消息
     */
    private Message message;
    /**
     * 异常类型 class全名
     */
    private String exceptionType;
    /**
     * 异常堆栈信息
     */
    private String exceptionDetail;
    /**
     * 消息重试次数
     */
    private Integer retryCount;
    /**
     * 消息最后一次失败的时间
     */
    private Date failTime;
    /**
     * 重新入原队列的时间
     */
    private Date redeliverTime;
    /**
     * 重新入原队列的操作人
     */
    private String redeliverUser;
}
