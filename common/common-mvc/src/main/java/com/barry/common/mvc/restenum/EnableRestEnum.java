package com.barry.common.mvc.restenum;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RestEnumRegistrar.class)
public @interface EnableRestEnum {

    /**
     * base packages，用于扫描带注解的枚举
     * @return array of basePackages
     */
    String[] basePackages() default {};

    /**
     * 传入枚举类名称，指定要使用的枚举类
     * @return
     */
    String[] classNames() default {};
}
