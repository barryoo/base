package com.barry.common.spring.annotion;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @author barrychen
 *
 * 由于 @ConditionalOnProperty 没有提供"当proper存在且为任意值时,触发条件"的特性, 所以使用该注解实现.
 * 当 value 对应的proper"被配置且有值不为空"时, 会被触发.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionalOnPropertyNotEmpty.OnPropertyNotEmptyCondition.class)
public @interface ConditionalOnPropertyNotEmpty {
    String value();

    /**
     * ConditionalOnPropertyNotEmpty condition
     */
    class OnPropertyNotEmptyCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> attrs = metadata.getAnnotationAttributes(ConditionalOnPropertyNotEmpty.class.getName());
            String propertyName = (String) attrs.get("value");
            String val = context.getEnvironment().getProperty(propertyName);
            return val != null && !val.trim().isEmpty();
        }
    }
}
