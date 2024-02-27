package com.barry.common.core.util;

import com.barry.common.core.bean.BeanConstraintViolationResult;
import com.google.common.collect.Lists;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>参数校验工具类（手动）</p>
 * 当需要手动校验 Bean 参数时，请直接使用该工具类
 *
 * @author chenpeng
 * Create at March 12, 2019 at 15:10:54 GMT+8
 */
public final class BeanValidatorUtils {

    private static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator DEFAULT_VALIDATOR = DEFAULT_VALIDATOR_FACTORY.getValidator();

    private BeanValidatorUtils() {
    }

    /**
     * <p>校验 Bean 的方法，JSR303</p>
     *
     * @author chenpeng
     * Create at March 12, 2019 at 15:18:07 GMT+8
     */
    public static <T> BeanConstraintViolationResult validate(T bean) {
        List<String> errors = null;
        Set<ConstraintViolation<T>> constraintViolations = DEFAULT_VALIDATOR.validate(bean);
        Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();

        if (iterator != null) {
            errors = Lists.newArrayList();
            while (iterator.hasNext()) {
                ConstraintViolation<T> error = iterator.next();
                errors.add(String.format("%s %s", error.getPropertyPath(), error.getMessage()));
            }
        }

        return new BeanConstraintViolationResult(errors == null || errors.size() == 0, errors);
    }

    public static <T> void validOrThrow(T bean, Class<?>... groups){
        Set<ConstraintViolation<T>> constraintViolations  = DEFAULT_VALIDATOR.validate(bean, groups);
        if(constraintViolations != null && constraintViolations.size() > 0){
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
