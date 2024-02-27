package com.barry.common.mvc.config.validator;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * <p>Bean 验证器配置</p>
 *
 * @author chenpeng
 * Create at May 30, 2019 at 15:36:05 GMT+8
 */
@Configuration
public class ValidatorExtensionAutoConfiguration {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation
                .byProvider(HibernateValidator.class)
                .configure()
                .failFast(false)
                .allowOverridingMethodAlterParameterConstraint(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
