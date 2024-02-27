package com.barry.common.core.util;

import com.barry.common.core.exception.BusinessException;
import com.barry.common.core.exception.ErrorCode;
import com.barry.common.core.exception.RequestClientException;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * check相关方法, 用于判断,并抛出异常.
 * default相关方法, 类似三元表达式
 * apply相关方法, 用于判断, 并执行.
 *
 * @author barry chen
 * @date 2021/1/20 7:34 下午
 */
public final class Assert {
    private Assert() {
    }

    public static void validate(boolean expression, String message) {
        check(expression, () -> new RequestClientException(message));
    }

    public static void validate(boolean expression, ErrorCode errorCode, String message) {
        check(expression, () -> new RequestClientException(errorCode, message));
    }

    /**
     * 如果表达式false, 则抛出异常.
     *
     * @param expression
     * @param exceptionSupplier
     */
    public static <X extends Throwable> void check(boolean expression, Supplier<? extends X> exceptionSupplier) throws X {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 如果表达式false, 则抛出异常.
     *
     * @param expression
     * @param message
     */
    public static void check(boolean expression, String message) {
        check(expression, () -> new BusinessException(message));
    }

    /**
     * 如果表达式false, 则抛出异常.
     *
     * @param expression
     * @param errorCode
     * @param message
     */
    public static void check(boolean expression, ErrorCode errorCode, String message) {
        check(expression, () -> new BusinessException(errorCode, message));
    }

    /**
     * 判断对象是否为null, 如果为null, 抛出异常
     *
     * @param obj
     * @param message
     */
    public static void checkNonNull(Object obj, String message) {
        check(Objects.nonNull(obj), () -> new BusinessException(message));
    }

    /**
     * 判断对象是否为null, 如果为null, 抛出异常
     *
     * @param obj
     * @param errorCode
     * @param message
     */
    public static void checkNonNull(Object obj, ErrorCode errorCode, String message) {
        check(Objects.nonNull(obj), () -> new BusinessException(errorCode, message));
    }

    /**
     * 判断对象是否为null, 如果为null, 抛出异常
     *
     * @param obj
     * @param exceptionSupplier
     */
    public static <X extends Throwable> void checkNonNull(Object obj, Supplier<? extends X> exceptionSupplier) throws X {
        check(Objects.nonNull(obj), exceptionSupplier);
    }

    /**
     * 判断字符串是否blank, 如果blank, 抛出异常
     *
     * @param str
     * @param message
     */
    public static void checkNotBlank(String str, String message) {
        check(StringUtils.isNotBlank(str), () -> new BusinessException(message));
    }

    /**
     * 判断字符串是否blank, 如果blank, 抛出异常
     *
     * @param str
     * @param errorCode
     * @param message
     */
    public static void checkNotBlank(String str, ErrorCode errorCode, String message) {
        check(StringUtils.isNotBlank(str), () -> new BusinessException(errorCode, message));
    }

    /**
     * 判断字符串是否blank, 如果blank, 抛出异常
     *
     * @param str
     * @param exceptionSupplier
     */
    public static <X extends Throwable> void checkNotBlank(String str, Supplier<? extends X> exceptionSupplier) throws X {
        check(StringUtils.isNotBlank(str), exceptionSupplier);
    }

    /**
     * 判断集合是否为空, 如果空, 抛出异常
     *
     * @param collection
     * @param message
     */
    public static void checkNotEmpty(Collection<?> collection, String message) {
        check(CollectionUtils.isNotEmpty(collection), () -> new BusinessException(message));
    }

    /**
     * 判断集合是否为空, 如果空, 抛出异常
     *
     * @param collection
     * @param errorCode
     * @param message
     */
    public static void checkNotEmpty(Collection<?> collection, ErrorCode errorCode, String message) {
        check(CollectionUtils.isNotEmpty(collection), () -> new BusinessException(errorCode, message));
    }

    /**
     * 判断集合是否为空, 如果空, 抛出异常
     *
     * @param collection
     * @param exceptionSupplier
     */
    public static <X extends Throwable> void checkNotEmpty(Collection<?> collection, Supplier<? extends X> exceptionSupplier) throws X {
        check(CollectionUtils.isNotEmpty(collection), exceptionSupplier);
    }

    /**
     * 如果assertFunction执行结果为true, 则执行runnable
     *
     * @param t
     * @param assertFunction
     * @param runnable
     * @param <T>
     */
    public static <T> void apply(T t, @Nonnull Function<T, Boolean> assertFunction, @Nonnull Runnable runnable) {
        apply(assertFunction.apply(t), runnable);
    }

    public static void apply(boolean expression, Runnable runnable) {
        if (expression) {
            runnable.run();
        }
    }

    /**
     * 如果assertFunction执行结果为true, 则执行consumer
     *
     * @param t
     * @param assertFunction
     * @param consumer
     * @param <T>
     */
    public static <T> void apply(T t, Function<T, Boolean> assertFunction, Consumer<T> consumer) {
        if (assertFunction.apply(t)) {
            consumer.accept(t);
        }
    }

    /**
     * 如果assertFunction执行结果为true, 则执行trueFunction, 否则执行falseFunction
     *
     * @param t
     * @param assertFunction
     * @param trueFunction
     * @param falseFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R apply(@Nonnull T t, @Nonnull Function<T, Boolean> assertFunction, @Nonnull Function<T, R> trueFunction, @Nonnull Function<T, R> falseFunction) {
        if (assertFunction.apply(t)) {
            return trueFunction.apply(t);
        } else {
            return falseFunction.apply(t);
        }
    }

    /**
     * 如果t不为null, 则执行trueFunction, 否则执行falseFunction
     * @param t
     * @param trueFunction
     * @param falseFunction
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T,R> R applyIfNonNull(@Nonnull T t, @Nonnull Function<T, R> trueFunction, @Nonnull Function<T, R> falseFunction) {
        return apply(t, Objects::nonNull, trueFunction, falseFunction);
    }

    /**
     * 如果字符串notBlank, 则执行consumer
     * @param t
     * @param trueFunction
     * @param falseFunction
     * @return
     * @param <R>
     */
    public static <R> R applyIfNotBlank(String t, Function<String, R> trueFunction, Function<String, R> falseFunction) {
        return apply(t, StringUtils::isNotBlank, trueFunction, falseFunction);
    }

    /**
     * 如果集合不为空, 则执行consumer
     * @param collection
     * @param trueFunction
     * @param falseFunction
     * @return
     * @param <R>
     */
    public static <R> R applyIfNotEmpty(Collection<?> collection, Function<Collection<?>, R> trueFunction, Function<Collection<?>, R> falseFunction) {
        return apply(collection, c->Objects.nonNull(c) && c.size()>0, trueFunction, falseFunction);
    }

    public static <T> T defaultIfNull(T t, T defaultT) {
        return defaultIfTrue(t, Objects::isNull, defaultT);
    }

    /**
     * 如果assertFunction执行结果为True, 则返回defaultT
     *
     * @param t
     * @param assertFunction
     * @param defaultT
     * @param <T>
     * @return
     */
    public static <T> T defaultIfTrue(T t, Function<T, Boolean> assertFunction, T defaultT) {
        if (assertFunction.apply(t)) {
            return defaultT;
        } else {
            return t;
        }
    }

    public static String defaultIfBlank(String str, String defaultStr) {
        return defaultIfTrue(str, StringUtils::isBlank, defaultStr);
    }

    public static Collection<?> defaultIfEmpty(Collection<?> collection, Collection<?> defaultCollection) {
        return defaultIfTrue(collection, CollectionUtils::isEmpty, defaultCollection);
    }

}
