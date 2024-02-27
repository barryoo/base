package com.barry.common.spring.util;

import com.barry.common.core.exception.BusinessException;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;

import java.io.IOException;

/**
 * @author barry chen
 * @date 2023/1/31 16:48
 */
public class JdkSerializeUtils {
    private static final DefaultSerializer SERIALIZER = new DefaultSerializer();
    private static final DefaultDeserializer DESERIALIZER = new DefaultDeserializer();

    public static byte[] serialize(Object object)  {
        try {
            return SERIALIZER.serializeToByteArray(object);
        } catch (IOException e) {
            throw new BusinessException("serialize fail. object:" + object.toString(), e);
        }
    }

    public static Object deserialize(byte[] bytes) {
        try {
            return DESERIALIZER.deserializeFromByteArray(bytes);
        } catch (IOException e) {
            throw new BusinessException("deserialize fail.", e);
        }
    }
}
