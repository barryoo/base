# auto transmitter

    微服务之间RPC调用时,自动传输所需对象.

## 说明

* 微服务之间RPC调用时,自动传输所需对象.需要传输的对象可以全局统一配置.传输的对象放在http header中. 当被调用方需要使用调用传输
  的对象时,可以直接获取到.

* 当前实现了feign调用时自动置入传输对象, 收到请求时,解析对象.

## 举例

服务A调用服务B, A可以把User对象置入transmitter, 当服务B接收到请求后, 可以直接获取到User对象.

## 使用说明

1. 引入依赖
2. 需要传输对象的类, 实现`Holder`
3. 在application.properties中配置
    ```
    spring.transmitter.enable=true
    ```
    * spring.transmitter.enable 是否启用auto transmitter
4. 使用java config配置`Holder`
```java
    @PostConstruct
    public void initAutoTransmitter() {
        HolderContext.config(Lists.newArrayList(VersionHolder.class));
    }
```
4. 调用`HolderContext.get(Class)`即可获得传输对象

## 注意

web容器对http请求头的长度有限制, 所以尽量不要传输较大对象.
如果一定要传输大对象, 需要修改web容器配置.

## 更新日志

### 1.2.1
base-pom 1.13.0
配置Holder的方式变更. 原本通过配置文件来指定Holder, 现在需要通过java config的形式来执行Holder.
原
```
    spring.transmitter.enable=true
    spring.transmitter.holders[0]=com.barry.system.service.transmitter.HolloWorldHolder
    spring.transmitter.holders[1]=com.barry.system.service.transmitter.User
```

现
```properties
spring.transmitter.enable=true
```

```java
    @PostConstruct
    public void initAutoTransmitter() {
        HolderContext.config(Lists.newArrayList(VersionHolder.class));
    }
```


### 1.2.0
base-pom 1.12.1
HolderContext.config方法的入参由`List<Class<Holder>>`改为`List<Class<? extends Holder>>` 
