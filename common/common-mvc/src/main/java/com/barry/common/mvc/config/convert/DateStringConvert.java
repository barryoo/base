package com.barry.common.mvc.config.convert;

import com.barry.common.core.util.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @author barry chen
 * @date 2020/11/19 4:13 下午
 */
public class DateStringConvert implements Converter<Date, String> {
    @Override
    public String convert(Date source) {
        return DateUtils.format(source);
    }
}
