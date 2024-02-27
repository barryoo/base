package com.barry.spring.boot.starter.amqp.plus;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpIllegalStateException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 延迟重试队列
 *
 * @author chen
 * @date 2020-02-18
 */
@Component
public class RabbitMqRetryHelper {

    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final String X_MESSAGE_TTL = "x-message-ttl";
    public static final String X_EXCEPTION = "x-exception";
    public static final String X_ORI_EXCHANGE = "x-ori-exchange";
    public static final String X_ORI_ROUTE_KEY = "x-ori-route-key";
    public static final String X_RETRY_COUNT = "x-retry-count";
    public static final String RETRY_EXCHANGE = "retry-exchange";
    public static final String RETRY_ROUTE_KEY = "retry";
    public static final String RETRY_QUEUE = "retry-queue";
    public static final String DELAY_EXCHANGE = "delay-exchange";
    public static final String DELAY_QUEUE = "delay-queue";
    public static final String DELAY_ROUTE_KEY = "delay";
    public static final String FAIL_EXCHANGE = "fail-exchange";
    public static final String FAIL_ROUTE_KEY = "fail";
    public static final String FAIL_QUEUE = "fail-queue";
    /**
     * 失败队列中,消息的最大存活时间
     */
    private static final Long FAIL_MAX_EXPIRATION_TIME = 14L * 24 * 3600 * 1000;
    private static final Long DELAY_MAX_EXPIRATION_TIME = 12L * 3600 * 1000;
    public final RabbitTemplate rabbitTemplate;

    public RabbitMqRetryHelper(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发送消息进入重试队列,达到重试上限后,进入fail队列.
     * 如果消息没有使用exchange, 使用该方法.
     *
     * @param routeKey    原路由(队列)
     * @param message     消息
     * @param retryTimes  想要重试的次数,默认1次.如果传入0,则不重试
     * @param delayPeriod 每次重试的间隔, 单位:毫秒
     * @param toFail      达到重试上限后,是否进入fail队列
     */
    public void retry(String routeKey, Message message, Integer retryTimes, Integer delayPeriod, boolean toFail) {
        retry(null, routeKey, message, retryTimes, delayPeriod, toFail, null, null);
    }

    public void retry(String routeKey, Message message, Integer retryTimes, Integer delayPeriod, IFailMessageProcessor processor) {
        retry(null, routeKey, message, retryTimes, delayPeriod, processor == null, processor, null);
    }

    /**
     * 发送消息进入重试队列,达到重试上限后,进入fail队列
     * 如果消息使用exchange, 使用该方法.
     *
     * @param exchange    原交换机(主题)
     * @param routeKey    原路由(队列)
     * @param message     消息
     * @param retryTimes  想要重试的次数,默认1次.如果传入0,则不重试
     * @param delayPeriod 每次重试的间隔, 单位:毫秒
     * @param toFail      达到重试上限后,是否进入fail队列
     */
    public void retry(String exchange, String routeKey, Message message, Integer retryTimes, Integer delayPeriod, boolean toFail) {
        retry(exchange, routeKey, message, retryTimes, delayPeriod, toFail, null, null);
    }

    public void retry(String routeKey, Message message, Integer retryTimes, Integer delayPeriod, IFailMessageProcessor processor, Exception ex) {
        retry(rabbitTemplate.getExchange(), routeKey, message, retryTimes, delayPeriod, processor == null, processor, ex);
    }

    public void retry(String exchange, String routeKey, Message message, Integer retryTimes, Integer delayPeriod, IFailMessageProcessor processor, Exception ex) {
        retry(exchange, routeKey, message, retryTimes, delayPeriod, processor == null, processor, ex);
    }

    public void retry(String exchange, String routeKey, Message message, Integer retryTimes, Integer delayPeriod, boolean toFail, IFailMessageProcessor processor,
            Exception ex) {
        if (retryTimes == null) {
            retryTimes = 1;
        }
        if (retryTimes <= 0) {
            return;
        }
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (!StringUtils.isBlank(exchange)) {
            message.getMessageProperties().setHeader(X_ORI_EXCHANGE, exchange);
        }
        message.getMessageProperties().setHeader(X_ORI_ROUTE_KEY, routeKey);
        //设置重试次数
        if (retryTimes != null && retryTimes > 0) {
            Integer retryCount;
            if (headers.get(X_RETRY_COUNT) != null) {
                retryCount = (Integer) headers.get(X_RETRY_COUNT);
            } else {
                retryCount = 0;
            }
            if (retryCount >= retryTimes) {
                if (processor != null) {
                    processor.process(message, ex);
                } else if (toFail) {
                    rabbitTemplate.send(FAIL_EXCHANGE, FAIL_ROUTE_KEY, message);
                }
                return;
            }
            message.getMessageProperties().setHeader(X_RETRY_COUNT, retryCount + 1);
        }
        //设置消息的延迟时间,优先级高于延迟队列的覆盖延迟队列的默认时间
        if (delayPeriod != null) {
            message.getMessageProperties().setExpiration(delayPeriod.toString());
        }
        rabbitTemplate.send(DELAY_EXCHANGE, DELAY_ROUTE_KEY, message);
    }

    /**
     * 转换Object为Message, 然后发送. 延迟一定时间后,才会被消费.
     * 没有指定exchange,发送到默认exchange {@link RabbitTemplate#getExchange()}
     * @param routingKey  路由(队列)
     * @param object      消息
     * @param delayPeriod 每次重试的间隔, 单位:毫秒
     */
    public void convertAndSendWithDelay(String routingKey, Object object, Integer delayPeriod){
        convertAndSendWithDelay(rabbitTemplate.getExchange(), routingKey, object, delayPeriod);
    }

    /**
     * 转换Object为Message, 然后发送. 延迟一定时间后,才会被消费
     *
     * @param exchange    交换机(主题)
     * @param routingKey  路由(队列)
     * @param object      消息
     * @param delayPeriod 每次重试的间隔, 单位:毫秒
     */
    public void convertAndSendWithDelay(String exchange, String routingKey, Object object, Integer delayPeriod){
        Message message = convertMessageIfNecessary(object);
        message.getMessageProperties().setHeader(X_ORI_EXCHANGE, exchange);
        message.getMessageProperties().setHeader(X_ORI_ROUTE_KEY, routingKey);
        message.getMessageProperties().setExpiration(delayPeriod.toString());
        rabbitTemplate.send(DELAY_EXCHANGE, DELAY_ROUTE_KEY, message);
    }

    public Message convertMessageIfNecessary(final Object object) {
        if (object instanceof Message) {
            return (Message) object;
        }
        MessageConverter converter = rabbitTemplate.getMessageConverter();
        if (converter == null) {
            throw new AmqpIllegalStateException("No 'messageConverter' specified. Check configuration of RabbitTemplate.");
        }
        return converter.toMessage(object, new MessageProperties());
    }

    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new ConcurrentHashMap<>(3);
        //设置 当delayQueue的消息超时后, 自动进入retryExchange
        args.put(X_DEAD_LETTER_EXCHANGE, RETRY_EXCHANGE);
        //设置 进入retryExchange时,携带的routeKey
        args.put(X_DEAD_LETTER_ROUTING_KEY, RETRY_ROUTE_KEY);
        //设置 队列中消息的最大超时时间, 优先级低于消息的最大超时时间.
        args.put(X_MESSAGE_TTL, DELAY_MAX_EXPIRATION_TIME);
        return QueueBuilder.durable(DELAY_QUEUE).withArguments(args).build();
    }

    @Bean
    public TopicExchange delayExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange(DELAY_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding delayBinding(@Qualifier("delayQueue") Queue queue, @Qualifier("delayExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DELAY_ROUTE_KEY);
    }

    @Bean
    public Queue retryQueue() {
        return new Queue(RETRY_QUEUE);
    }

    @Bean
    public TopicExchange retryExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange(RETRY_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding retryBinding(@Qualifier("retryQueue") Queue queue, @Qualifier("retryExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RETRY_ROUTE_KEY);
    }

    @Bean
    public Queue failQueue() {
        Map<String, Object> args = new ConcurrentHashMap<>(3);
        args.put(X_MESSAGE_TTL, FAIL_MAX_EXPIRATION_TIME);
        return QueueBuilder.durable(FAIL_QUEUE).withArguments(args).build();
    }

    @Bean
    public TopicExchange failExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange(FAIL_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding failBinding(@Qualifier("failQueue") Queue queue, @Qualifier("failExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(FAIL_ROUTE_KEY);
    }

    /**
     * retry队列消费者,把消息发送到原exchange
     *
     * @param message
     */
    @RabbitListener(queues = "retry-queue")
    public void forwardMessage(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        rabbitTemplate.send((String) headers.get(X_ORI_EXCHANGE), (String) headers.get(X_ORI_ROUTE_KEY), message);
    }

}
