package com.barry.common.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public class JacksonComponent {

    /**
     * 解析日期字符串
     */
    public static class DateJsonDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return DateUtils.parseDate(jsonParser.getText());
        }
    }

    /**
     * 集合序列化器 null转换为[]
     */
    public static class NullCollectionSerializer extends JsonSerializer<Object>{

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            gen.writeEndArray();
        }
    }

    /**
     * String序列化器 null转换为""
     */
    public static class NullStringSerializer extends JsonSerializer<Object> {

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString("");
        }
    }

    /**
     * Map序列化器 null转换为{}
     */
    public static class NullMapSerializer extends JsonSerializer<Object> {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeEndObject();
            }
    }

    /**
     * 处理NULL的序列化.
     * - 对于集合类型, 序列化为[]
     * - 对于字符串类型, 序列化为""
     */
    public static class NullJsonSerializerModifier extends BeanSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            beanProperties.forEach(beanPropertyWriter -> {
                Class<?> clazz = beanPropertyWriter.getType().getRawClass();
                if (CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz)) {
                    beanPropertyWriter.assignNullSerializer(new NullStringSerializer());
                }

/*                if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
                    beanPropertyWriter.assignNullSerializer(new NullCollectionSerializer());
                } else if (Map.class.isAssignableFrom(clazz)) {
                    beanPropertyWriter.assignNullSerializer(new NullMapSerializer());
                }*/
            });

            return super.changeProperties(config, beanDesc, beanProperties);
        }
    }
}
