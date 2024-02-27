# common

## common-core
提供基础工具类, 任何java项目都可以引入.
* 统一请求响应模型
* 数据库分页
* FTP/SFTP
* Mail
* Http工具
* 常用常量
  * 是否/真假
  * 符号
  * 邮箱服务提供商
* 常用枚举
  * 货币
  * 时间格式
  * 文件类型
  * 长度/重量单位与转换
* 异常
  * 各种异常类型
* 工具
  * Opt: 更强大的Optional
  * 断言
  * 对象复制
  * 字段校验
  * 常用类型转换
  * 日期
  * 环境
  * 文件/文件类型
  * json
  * 多种随机数生成
  * 日志工具
  * 文件解压缩工具
  * DOM解析

## common-spring
主要提供common-core在spring环境下自动装配, 任何spring项目都可以引入.
* 分布式ID自动装配. workId防重复.
* JSON
  * jackson自动装配, 提供常用的序列化/反序列化器. 优先使用spring-boot的jackson配置.
  * Json工具类, 优先使用spring-boot提供的ObjectMapper
* 缓存
  * ehcache
  * redis
    * 实现key级别的过期时间控制.
    * redis工具类
* ribbon
  * 修改默认的重试策略. 只有当发生连接超时异常时,才会进行重试, 其他情况不重试. 避免ReadTimeout重试引发性能灾难.
* spring环境工具
* spring context工具

## common-mvc
对spring-mvc各层常用类库和工具的整合.
* 开发环境多泳道控制
* mybatis-plus自动装配. 自动刷新mapper.xml
* 各种类型的请求参数转换
* 统一异常处理
* 请求范围内自动注入StopWatchRecorder
* RestEnum

## common-config
在该模块中统一管理各个服务的公共配置项.

### 用法
其他服务可以通过引入该模块来获取公共配置项.在服务的maven配置文件pom.xml中引入此依赖即可.
不同的配置文件,有不同的用法以及覆盖方式.

### spring application.properties
在`application.properties`中管理, `classpath:application.properties`是springBoot默认的配置文件位置之一. 见 https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.files
如果你想要覆盖`application.properties`中的某个配置项,可以在你的服务的`config/application.properties`中添加相同的配置项即可.
如果你的服务中`application.properties`在resource根目录下, 则common-config的`application.properties`不会被扫描到.

文件目录和文件名都不能改变,否则某些配置项无效.
如果不使用springboot的默认配置文件位置, 则需要通过`@PropertySources`引入. 但是由于logging相关配置在ApplicationContext初始化之前, 所以通过 `@PropertySources`引入的logging配置是无效的.
事实上,不仅仅是logging配置, 还有其他一些ApplicationContext初始化之前的配置也无法在此处使用,如`spring.main.banner-mode`.
https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging.custom-log-configuration

```markdown
Spring Boot will automatically find and load application.properties and application.yaml files from the following locations when your application starts:

1. From the classpath
    a. The classpath root
    b. The classpath /config package
2. From the current directory
    a. The current directory
    b. The config/ subdirectory in the current directory
    c. Immediate child directories of the config/ subdirectory
```

### log4j2
`log/log4j/log4j2-spring-{profile}.xml`用于springboot的日志配置.
通过在`application.properties`文件中配置`logging.config`来指定日志配置文件的位置. 其他服务无需手动启用.
如果你想要覆盖日志配置,可以在你的服务的`application.properties`中配置`logging.config`来指定自定义的日志配置文件的位置.
如果要覆盖,需要覆盖所有`profile`的日志配置文件.


## 开发环境多泳道控制
说明:用户开发环境, 多个开发者不同代码版本下,互不干扰调用. 根据请求头识别永道,调用对用的服务.

在local和dev环境下自动装配,全局生效. 使用者只需要引入common-mvc, 并配置`eureka.instance.metadata-map.version`即可, 不需要其他任何配置.
* 自动通过 @RibbonClients(defaultConfiguration = {LoadBalanceVersionRuleConfiguration.class}) 实现全局配置VersionRule
* 自动通过 VersionHolderConfiguration 启用auto-transmitter, 并配置VersionHolder

如果使用者想要自主控制不同的服务使用不同的负载均衡规则,需要进行以下操作:
* 通过@ComponentScan exclude排除LoadBalanceVersionRuleAutoConfiguration.
```java
@ComponentScan(value = "com.barry",
        excludeFilters = {@ComponentScan.Filter(type=ASSIGNABLE_TYPE, classes = LoadBalanceVersionRuleAutoConfiguration.class)})
```
* 在application-{profile}.properties文件中为每个服务指定负载均衡策略
```properties
system.ribbon.NFLoadBalancerRuleClassName=com.barry.common.mvc.loadbalance.rule.VersionRule
```

## Ribbon配置与重试策略
* 通过common-config统一控制所有环境的Ribbon配置.
    * connectTimeout 2000s
    * readTimeout 5000s 
    * maxAutoRetries 1
    * maxAutoRetriesNextServer 1
    * 所有请求方式都会进行重试.
* 重试机制
    * 只有connectTimeout才进行重试, 避免在接口效率低时, 多次重试导致性能更差, 进而引发雪崩.

修改后的重试策略为:
* 连接超时: 重试sameServer1次, 重试nextServer2次
* 读取超时: 完全不重试.
* 对所有请求方法(如:GET POST PUT DELETE)都进行重试.

### IEnum

1. 建议enum继承`IEnum`,并实现`parse(String)`方法. 可以支持spring mvc 入参中的enum自动转换
2. enum尽量保证有一个主成员变量,并且该成员变量名字与enum名字一致.  
   这样就可以支持feign调用时,自动转换.

例如 `OSSACL`. 成员变量名字是 `ossAcl`, 实现了parse(String)方法.

```
public enum OSSACL implements IEnum {

    Default("default"),
    Private("private"),
    PublicRead("public-read");

    @Getter
    private String ossAcl;

    private OSSACL(String ossAcl) {
        this.ossAcl = ossAcl;
    }

    public static OSSACL parse(String ossAcl) {
        for (OSSACL a : OSSACL.values()) {
            if (a.toString().equals(ossAcl)) {
                return a;
            }
        }

        throw new IllegalArgumentException("Unable to parse the provided acl " + ossAcl);
    }

    @Override
    public String toString() {
        return this.ossAcl;
    }
}

```

### Exception

业务系统中, 如果要抛出业务异常, 可以直接使用`BusinessException`,不要直接抛出`RuntimeException`
业务系统中, 使用errorCode, 需要实现`ErrorCode`.

### json

- 非spring项目中使用`JsonMapper`,可以指定自定义的`ObjectMapper`, 如果不指定则使用默认的.
- 在springboot项目中,使用`JsonUtils`, 默认使用springboot中提供的`ObjectMapper`,对`JsonMapper`和`JsonUtils`进行初始化, 所以使用springboot的jackson配置, 对JsonUtils也有效.
- 如果有特殊需要,需要使用自定义的`ObjectMapper`, 可以自己实现,并初始化新的`JsonMapper`.
- 针对不同的接口,有不同的序列化特性
  - 如果接口的响应为ApiResult,则NULL字段依然会被序列化, 且字符串为NULL时被序列化为"".
  - 如果接口的响应不是ApiResult,则NULL字段不会被序列化.

### CommonsMultipartFile

文件上传,统一使用`CommonsMultipartFile`, 配置继承自springboot的`MultipartProperties`

### mybtais-plus

1. 实现了mybatis的mapper.xml自动刷新.
2. 无需使用`@MapperScan`注解. 自动扫描"%s.**.persistence.mapper"包下的所有实现了`CrudMapper`的接口.
3. 已配置分页插件.

### UnifiedExceptionHandler
全局统一异常处理.

如果有新的统一异常需要处理,请在该类中添加.

在手动抛出异常时,根据场景,选择合适的异常类型. 不同异常类型,会响应不同的http code.
* MethodArgumentNotValidException 仅限于接口参数校验失败, spring的异常类, http code 为400
* ConstraintViolationException jsr303校验失败, jdk的异常类, http code 为400
* 抛出BusinessException只打印warn日志, 不会打印error日志, http code 为500

### feign
* 对服务间调用中出现的异常,已经进行统一处理, 全部转为 `ApplicationException`或`BusinessException`.
* 实现远程调用的异常传递.

### ribbon
在common-config统一配置ribbon, 如果想要修改,请在自己的服务中覆盖配置项.
通过`CustomRibbonLoadBalancedRetryFactory`重写了ribbon的重试机制, 仅在连接超时重试.

### BeanSelfAware

实现该接口, 可以自我注入.

### RestEnum
- 提供统一web接口， 查询"允许对外暴露"的Enum。
  - GET /项目名称/restEnum/{enumClassName}
- @EnableRestEnum 在启动类上标记启用该功能, basePackage指定将要扫描的包, classNames指定将要扫描的枚举类
- @RestEnum 在枚举类上标记, 表示改枚举可以被web接口查到
- @RestEnumKey 在枚举类属性上标记, 指定接口返回值中的key，在枚举中最多只能存在一个, 如果没有则key等于默认name
- @RestEnumValue 在枚举类属性上标记, 指定接口返回值中的value，在枚举中最多只能存在一个,如果没有则value等于默认ordinal
- @RestEnumProperty 在枚举类属性上标记, 指定其他可以被接口返回的属性和属性值
