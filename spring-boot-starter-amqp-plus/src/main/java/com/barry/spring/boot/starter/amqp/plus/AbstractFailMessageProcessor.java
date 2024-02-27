package com.barry.spring.boot.starter.amqp.plus;

import com.barry.common.core.util.UUIDUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 通常建议基于该类实现失败消息的处理.
 *
 * @author barry chen
 * @date 2021/3/16 10:30
 */
public abstract class AbstractFailMessageProcessor implements IFailMessageProcessor {
    private static final int RETRY_COUNT = 0;

    @Override
    public void process(Message message, Exception ex) {
        Date now = new Date();
        MessageProperties properties = message.getMessageProperties();
        FailMessage failMessage = new FailMessage();
        failMessage.setId(UUIDUtils.uuid());
        properties.setHeader("x-retry-count", RETRY_COUNT);
        String body;
        try {
            body = new String(message.getBody(), properties.getContentEncoding());
        } catch (UnsupportedEncodingException e) {
            body = new String(message.getBody(), StandardCharsets.UTF_8);
        }
        failMessage.setMessage(message);
        failMessage.setMessageBinary(SerializationUtils.serialize(message));
        failMessage.setMessageBody(body);
        failMessage.setMessageProperties(properties.toString());
        failMessage.setMessageId(properties.getMessageId());
        failMessage.setExceptionType(ex.getClass().getSimpleName());
        failMessage.setExceptionDetail(ExceptionUtils.getStackTrace(ex));
        failMessage.setFailTime(now);
        failMessage.setOriExchange(properties.getHeader(RabbitMqRetryHelper.X_ORI_EXCHANGE));
        failMessage.setOriRouteKey(properties.getHeader(RabbitMqRetryHelper.X_ORI_ROUTE_KEY));
        failMessage.setOriQueue(properties.getConsumerQueue());
        failMessage.setRetryCount(properties.getHeader(RabbitMqRetryHelper.X_RETRY_COUNT));

        doProcess(failMessage, ex);
    }

    /**
     * 处理failMessage
     *
     * @param failMessage
     * @param ex
     */
    public abstract void doProcess(FailMessage failMessage, Exception ex);

}
