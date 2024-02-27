package com.barry.common.mvc.config.convert;

import com.barry.common.core.util.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @author barry chen
 * @date 2020/9/18 3:02 下午
 */
public class StringDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        return DateUtils.parseDate(source);
    }
}
