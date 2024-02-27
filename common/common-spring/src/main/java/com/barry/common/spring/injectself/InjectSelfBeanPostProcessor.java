package com.barry.common.spring.injectself;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class InjectSelfBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BeanSelfAware) {
            BeanSelfAware myBean = (BeanSelfAware) bean;
            myBean.setSelf(bean);
            return myBean;
        }
        return bean;
    }

}
