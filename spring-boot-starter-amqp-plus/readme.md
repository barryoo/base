# AMQP配置增强

## MessageConverter

使用`Jackson2JsonMessageConverter`作为消息处理类

未来: 使用项目中自带的json处理类

## 延迟重试队列

调用retry方法, 可以使用延迟重试.

## 延迟队列
调用convertAndSendWithDelay方法, 可以使用延迟队列.
