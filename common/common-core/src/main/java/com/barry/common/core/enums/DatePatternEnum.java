package com.barry.common.core.enums;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author barry chen
 */
public enum DatePatternEnum {

    /**
     * 时间格式
     */
    DATE_TIME_MS_PATTERN(0, "yyyy-MM-dd HH:mm:ss.SSS", "年-月-日 时:分:秒.毫秒"),

    DATE_TIME_PATTERN(1, "yyyy-MM-dd HH:mm:ss", "年-月-日 时:分:秒"),

    TIME_PATTERN(2, "HH:mm:ss", "时:分:秒"),

    MINUTE_PATTERN(3, "yyyy-MM-dd HH:mm", "年-月-日 时:分"),

    DATE_PATTERN(4, "yyyy-MM-dd", "年-月-日"),

    MONTH_PATTERN(5, "yyyy-MM", "年-月"),

    ONLY_YEAR_PATTERN(6, "yyyy", "年"),

    ONLY_MONTH_PATTERN(7, "MM", "月"),

    ONLY_DAY_PATTERN(8, "dd", "日"),

    ONLY_HOUR_PATTERN(9, "HH", "时"),

    ONLY_MINUTE_PATTERN(10, "mm", "分"),

    ONLY_SECOND_PATTERN(11, "ss", "秒"),

    ZN_DATE_TIME_MS_PATTERN(12, "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒", "中文格式年月日时分秒毫秒"),

    ZN_DATE_TIME_PATTERN(13, "yyyy年MM月dd日 HH时mm分ss秒", "中文格式年月日时分秒"),

    ZN_DATE_PATTERN(14, "yyyy年MM月dd日", "中文格式年月日"),

    ZN_MONTH_PATTERN(15, "yyyy年MM月", "中文格式年月"),

    ZN_YEAR_ONLY_PATTERN(16, "yyyy年", "中文格式年"),

    ZN_TIME_PATTERN(17, "HH时mm分ss秒", "中文格式时分秒"),

    GAP_LESS_DATE_TIME_PATTERN(18, "yyyyMMddHHmmss", "无间隔符的年月日时分秒"),

    GAP_LESS_DATE_TIME_MS_PATTERN(19, "yyyyMMddHHmmssSSS", "无间隔符的年月日时分秒毫秒"),

    GAP_LESS_DATE_PATTERN(20, "yyyyMMdd", "无间隔符的年月日"),

    ISO_8601_PATTERN(21, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "ISO8601:2004 年-月-日T时:分:秒毫秒时差"),

    TIME_ZONE_DATE_TIME_PATTERN(22, "yyyy-MM-dd'T'HH:mm:ssZ", "年-月-日T时:分:秒时区"),
    ;

    private static final Map<DatePatternEnum, DateTimeFormatter> FORMATTER_CACHE = new WeakHashMap<>(initialCapacity());
    private final int index;
    private final String pattern;
    private final String desc;

    DatePatternEnum(int index, String pattern, String desc) {
        this.index = index;
        this.pattern = pattern;
        this.desc = desc;
    }

    private static int initialCapacity() {
        return (values().length & 1) == 1 ? values().length + 1 : values().length;
    }

    private static void checkCache() {
        if (FORMATTER_CACHE.isEmpty() || FORMATTER_CACHE.size() != values().length) {
            FORMATTER_CACHE.clear();
            for (DatePatternEnum datePatternEnum : values()) {
                FORMATTER_CACHE.put(datePatternEnum, DateTimeFormatter.ofPattern(datePatternEnum.getPattern()));
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDesc() {
        return desc;
    }

    public DateTimeFormatter getFormatter() {
        checkCache();
        return FORMATTER_CACHE.getOrDefault(this, DateTimeFormatter.ofPattern(getPattern()));
    }

}
