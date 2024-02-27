package com.barry.common.core.util;

import com.barry.common.core.enums.DatePatternEnum;
import com.barry.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

/**
 * <p>时间工具类</p>
 *
 * @author chenpeng
 * Create at January 24, 2019 at 14:36:57 GMT+8
 */
@Slf4j
public final class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss.SSS Z",
            "yyyy-MM-dd HH:mm:ss Z",
            "yyyy-MM-dd HH:mm Z",
            "yyyy-MM-dd HH Z",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH",
            "yyyy-MM-dd",
            "yyyy-MM",
            "yyyy/MM/dd HH:mm:ss.SSS Z",
            "yyyy/MM/dd HH:mm:ss Z",
            "yyyy/MM/dd HH:mm Z",
            "yyyy/MM/dd HH Z",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd HH",
            "yyyy/MM/dd",
            "yyyy/MM",
            "yyyy.MM.dd HH:mm:ss.SSS Z",
            "yyyy.MM.dd HH:mm:ss Z",
            "yyyy.MM.dd HH:mm Z",
            "yyyy.MM.dd HH Z",
            "yyyy.MM.dd HH:mm:ss.SSS",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm",
            "yyyy.MM.dd HH",
            "yyyy.MM.dd",
            "yyyy.MM",
            "yyyyMMdd",
            "yyyyMM",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
    };
    /**
     * 默认日期格式
     */
    private static final String DEFAULT_LOCAL_DATE_FMT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    private static final String DEFAULT_LOCAL_DATE_TIME_FMT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DEFAULT_DATE_TIME_FMT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private DateUtils() {
    }

    /**
     * <p>将 java.util.Date 类型转为 JDK8 LocalDateTime，使用系统默认时区</p>
     *
     * @param date 日期，java.util.Date
     * @return LocalDate
     * @author chenpeng
     * Create at January 24, 2019 at 14:42:17 GMT+8
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDate(date, null);
    }

    /**
     * <p>将 java.util.Date 类型转为 JDK8 LocalDate</p>
     *
     * @author chenpeng
     * Create at January 24, 2019 at 14:34:56 GMT+8
     */
    public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        LocalDate local = null;
        if (date != null) {
            zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
            Instant instant = date.toInstant();
            local = instant.atZone(zoneId).toLocalDate();
        }
        return local;
    }

    /**
     * <p>将字符串日期转换为 LocalDate ，格式为 yyyy-MM-dd</p>
     * 注意：LocalDate 不包含 HH:mm:ss 时间部分
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:05:29 GMT+8
     */
    public static LocalDate toLocalDate(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return toLocalDate(text, DEFAULT_LOCAL_DATE_FMT);
    }

    /**
     * <p>将给定字符串按照给定格式转换为 LocalDate </p>
     *
     * @param text   待转换的日期字符串
     * @param format text 参数字符串本身的格式
     * @author chenpeng
     * Create at February 13, 2019 at 16:06:52 GMT+8
     */
    public static LocalDate toLocalDate(String text, String format) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(format)) {
            return null;
        }
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(format));
    }

    /**
     * <p>将给定 Date 转换为 LocalDateTime </p>
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:08:56 GMT+8
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, null);
    }

    /**
     * <p>将 java.util.Date 类型转为 JDK8 LocalDateTime</p>
     *
     * @param date   日期，java.util.Date
     * @param zoneId 时区
     * @return LocalDateTime
     * @author chenpeng
     * Create at January 24, 2019 at 14:34:56 GMT+8
     */
    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        LocalDateTime local = null;
        if (date != null) {
            zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
            Instant instant = date.toInstant();
            local = instant.atZone(zoneId).toLocalDateTime();
        }
        return local;
    }

    /**
     * <p>将字符串日期转换为 LocalDateTime ，格式为 yyyy-MM-dd HH:mm:ss</p>
     * 注意：LocalDateTime 包含 HH:mm:ss 时间部分
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:05:29 GMT+8
     */
    public static LocalDateTime toLocalDateTime(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return toLocalDateTime(text, DEFAULT_LOCAL_DATE_TIME_FMT);
    }

    /**
     * <p>将给定字符串按照给定格式转换为 LocalDateTime </p>
     *
     * @param text   待转换的日期字符串
     * @param format text 参数字符串本身的格式
     * @author chenpeng
     * Create at February 13, 2019 at 16:06:52 GMT+8
     */
    public static LocalDateTime toLocalDateTime(String text, String format) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(format)) {
            return null;
        }
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 将 LocalDate 转换为 Date，采用系统默认时区
     *
     * @param localDate
     * @return Date
     * @author chenpeng
     * Create at January 24, 2019 at 15:06:12 GMT+8
     */
    public static Date toDate(LocalDate localDate) {
        return toDate(localDate, null);
    }

    /**
     * 将 LocalDate 转换为 Date
     *
     * @param localDate
     * @param zoneId
     * @return Date
     * @author chenpeng
     * Create at January 24, 2019 at 15:05:24 GMT+8
     */
    public static Date toDate(LocalDate localDate, ZoneId zoneId) {
        Date date = null;
        if (localDate != null) {
            zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
            ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
            date = Date.from(zdt.toInstant());
        }
        return date;
    }

    /**
     * <p>将 LocalDateTime 转为 Date</p>
     *
     * @author chenpeng
     * Create at January 24, 2019 at 15:11:10 GMT+8
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return toDate(localDateTime, null);
    }

    /**
     * <p>将 LocalDateTime 转为 Date</p>
     *
     * @param localDateTime
     * @param zoneId
     * @return Date
     * @author chenpeng
     * Create at January 24, 2019 at 15:10:29 GMT+8
     */
    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        Date date = null;
        if (localDateTime != null) {
            zoneId = Optional.ofNullable(zoneId).orElse(ZoneId.systemDefault());
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            date = Date.from(zdt.toInstant());
        }
        return date;
    }

    /**
     * <p>获取两个日期间相隔天数</p>
     * 注意：两个参数均表示“日期”，即不包含 时：分：秒 部分
     * 若传入 Date 包含 时：分：秒 则截断该部分
     *
     * @param d1
     * @param d2
     * @return Long
     * @author chenpeng
     * Create at January 24, 2019 at 15:37:11 GMT+8
     */
    public static Long getDaysBetween(Date d1, Date d2) {
        return getDaysBetween(toLocalDate(d1), toLocalDate(d2));
    }

    /**
     * <p>获取两个日期间相隔天数</p>
     * 注意：入参类型为 LocalDate，即“日期”，即不包含 时：分：秒 部分
     *
     * @param d1
     * @param d2
     * @return Long
     * @author chenpeng
     * Create at January 24, 2019 at 15:40:20 GMT+8
     */
    public static Long getDaysBetween(LocalDate d1, LocalDate d2) {
        Long days = null;
        if (d1 != null && d2 != null) {
            days = d1.until(d2, ChronoUnit.DAYS);
        }
        return days;
    }

    /**
     * <p>将给定日期按给定格式转换为字符串</p>
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:24:58 GMT+8
     */
    public static String format(LocalDate localDate, String format) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * <p>将给定日期换为字符串，格式为 yyyy-MM-dd</p>
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:24:58 GMT+8
     */
    public static String format(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return format(localDate, DEFAULT_LOCAL_DATE_FMT);
    }

    /**
     * <p>将给定时间按给定格式转换为字符串</p>
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:24:58 GMT+8
     */
    public static String format(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * <p>将给定时间换为字符串，格式为 yyyy-MM-dd HH:mm:ss</p>
     *
     * @author chenpeng
     * Create at February 13, 2019 at 16:24:58 GMT+8
     */
    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return format(localDateTime, DEFAULT_LOCAL_DATE_TIME_FMT);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String format(Date date, String pattern) {
        String formatDate = null;
        if (StringUtils.isNotBlank(pattern)) {
            formatDate = DateFormatUtils.format(date, pattern);
        } else {
            formatDate = DateFormatUtils.format(date, DEFAULT_DATE_TIME_FMT);
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String format(Date date) {
        return format(date, DEFAULT_DATE_TIME_FMT);
    }

    /**
     * 得到日期时间字符串，转换格式（yyyyMMdd）
     */
    public static String formatGapLessDays(Date date) {
        return format(date, DatePatternEnum.GAP_LESS_DATE_PATTERN.getPattern());
    }

    /**
     * 得到日期时间字符串，转换格式（yyyyMMddHHmmss）
     */
    public static String formatGapLessSeconds(Date date) {
        return format(date, DatePatternEnum.GAP_LESS_DATE_TIME_PATTERN.getPattern());
    }

    /**
     * 得到日期时间字符串，转换格式（yyyyMMddHHmmssSSS）
     */
    public static String formatGapLessMillis(Date date) {
        return format(date, DatePatternEnum.GAP_LESS_DATE_TIME_MS_PATTERN.getPattern());
    }

    /**
     * 转换为时间字符串（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String format(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime(Date date) {
        return format(date, "HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear(Date date) {
        return format(date, "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth(Date date) {
        return format(date, "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay(Date date) {
        return format(date, "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek(Date date) {
        return format(date, "E");
    }

    public static Date parseDate(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return parseDate(str.toString(), PARSE_PATTERNS);
    }

    public static Date parseDate(final String str, final String... parsePatterns) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, parsePatterns);
        } catch (ParseException e) {
            log.error("Error:parseDate(dateStr:{}, error:{})", str, e);
            throw new BusinessException(String.format("Error occur parseDate(dateStr:%s). error:%s", str, e.getMessage()), e);
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static int getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (int) (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * @Description 获取最大天数差
     * @Date ${DATE}
     */
    public static int getMaxDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        long res = afterTime - beforeTime;
        if (res < 0) {
            return 0;
        }
        return (int) ((afterTime - beforeTime) / (1000 * 60 * 60 * 24) + 0.5);
    }

    public static int getDistanceDayOfTwoDate(Date before, Date after) {
        Date beforeDate = getDayEndTime(before);
        Date afterDate = getDayEndTime(after);
        long res = afterDate.getTime() - beforeDate.getTime();
        if (res < 0) {
            return 0;
        }
        return (int) (res / (60 * 60 * 24 * 1000));
    }

    /**
     * 返回本周周一的date
     */
    public static Date getCurrentMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTime();
    }

    /**
     * 返回本周周日的date
     */
    public static Date getPreviousSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.MILLISECOND, 999);
        return currentDate.getTime();
    }

    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }

    /**
     * 获取某天的中午 2020-12-31 12:00:00.000
     *
     * @param startDate
     * @return
     */
    public static Date getDayMidTime(Date startDate) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(startDate);
        todayStart.set(Calendar.HOUR_OF_DAY, 12);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取某天的开始一刻 2020-12-31 00:00:00.000
     *
     * @param startDate
     * @return
     */
    public static Date getDayStartTime(Date startDate) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(startDate);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取某天的最后一刻 2020-12-31 23:59:59.999
     *
     * @param endDate
     * @return
     */
    public static Date getDayEndTime(Date endDate) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(endDate);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取某天的下一天的开始一刻 2021-01-01 00:00:00.000
     *
     * @param date
     * @return
     */
    public static Date getDateOfNextMorning(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 获取当月第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 根据入参时间返回当前月最后的时间
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 判断日期是否为月末
     */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) + 1));
        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取下个月的当前日期
     *
     * @param date
     * @return
     */
    public static Date getDayOfNextMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //把日期往后增加一个月.整数往后推,负数往前移动
        calendar.add(Calendar.MONTH, 1);
        date = calendar.getTime();
        return date;
    }

    public static Date getSpecialTime(Date date, int hour, int minute, int second) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, minute);
        todayStart.set(Calendar.SECOND, second);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取指定日期之后的第n个工作日
     *
     * @param date    指定日期
     * @param nextCnt 第几个工作日
     * @return 日期
     */
    public static Date getNextWorkingDay(Date date, int nextCnt) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null.");
        }
        if (nextCnt < 1) {
            throw new IllegalArgumentException("nextCnt must be greater than or equal to 1.");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < nextCnt; i++) {
            calendar.add(Calendar.DATE, 1);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 2);
            } else if (dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
            }
        }
        return calendar.getTime();
    }
}
