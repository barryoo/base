package com.barry.common.mvc.config.convert;

import com.barry.common.core.enums.IEnum;
import com.barry.common.core.util.StringUtils;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author barry chen
 * <p>
 * 服务间调用时, 对Enum--String进行自动转换.
 * 要使用此特性,需要满足以下条件
 * 1. 需要转换的Enum必须实现IEnum
 * 2. 需要转换的Enum必须实现 "Enum parse(String param)" 方法
 */
public class BaseEnumConverterFactory implements ConverterFactory<String, IEnum> {

    private static final Map<Class<? extends IEnum>, Converter<String, ? extends IEnum>> CONVERTER_MAP = new WeakHashMap<>();

    private static final String ENUM_CONVERT_METHOD_NAME = "parse";

    @SneakyThrows
    @Override
    public <T extends IEnum> Converter<String, T> getConverter(Class<T> targetType) {

        Converter<String, T> result = (Converter<String, T>) CONVERTER_MAP.get(targetType);
        if (result == null) {
            result = new StringToIEumConverter<T>(targetType);
            CONVERTER_MAP.put(targetType, result);
        }
        return result;
    }

    /**
     * String to Enum convert
     * @param <T>
     */
    private static class StringToIEumConverter<T extends IEnum> implements Converter<String, T> {

        private final Class<T> targetType;

        StringToIEumConverter(Class<T> targetType) {
            this.targetType = targetType;
        }

        @SneakyThrows
        @Override
        public T convert(String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            Method method = targetType.getDeclaredMethod(ENUM_CONVERT_METHOD_NAME, String.class);
            Object o = method.invoke(null, source);
            return (T) o;
        }
    }

}
