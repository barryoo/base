package com.barry.common.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.stream.IntStream;

/**
 * <p>UUID 工具类</p>
 *
 * @author chenpeng
 * Create at February 11, 2019 at 17:49:24 GMT+8
 */
public final class UUIDUtils {

    /**
     * 16 进制
     */
    private static final int HEXADECIMAL = 16;
    /**
     * 短 uuid 默认长度
     */
    private static final int SHORT_UUID_LENGTH = 8;
    /**
     * 字符表 0-9 a-z A-Z
     */
    private static final String[] ALPHABET_62 = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    /**
     * 字符表 0-9 a-z
     */
    private static final String[] ALPHABET_36 = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9"};

    /**
     * UUID 分隔符
     */
    private static final String UUID_SEPARATOR = "-";

    private UUIDUtils() {
    }

    /**
     * 获得 uuid，默认不带分隔符 “-”
     */
    public static String uuid() {
        return uuid(true);
    }

    /**
     * 通过给定参数指定返回 uuid 是否带有分隔符
     *
     * @param withoutSeparator 是否带分隔符
     * @return String
     */
    public static String uuid(boolean withoutSeparator) {
        String uuid = UUID.randomUUID().toString();
        if (withoutSeparator) {
            uuid = uuid.replaceAll(UUID_SEPARATOR, StringUtils.EMPTY);
        }
        return uuid;
    }

    /**
     * 生成短uuid.
     * ShortUUID碰撞概率比UUID高, 用于临时场景, 尽量不要用于长期存储且需保证唯一的场景.
     * 生成RandomUUID(16进制), 把RandomUUID分为8端,每段4位(0-) 对alphabetCode.length取余,得到的值作为索引取出alphabetCode中对应的字符.
     *
     * @return String
     */
    public static String shortUUID(String[] alphabetCode, int length) {
        int alphabetCodeLength = alphabetCode.length;
        StringBuilder shortUUID = new StringBuilder();

        String uuid = uuid();
        int step = uuid.length() / length;

        IntStream.range(0, length).forEach(i -> {
            String temp = uuid.substring(i * step, i * step + step);
            int value = Integer.parseInt(temp, HEXADECIMAL);
            shortUUID.append(alphabetCode[value % alphabetCodeLength]);
        });

        return shortUUID.toString();
    }

    /**
     * 生成短uuid
     * 字符集 0-9 a-z A-Z
     * @return
     */
    public static String shortUUID62() {
        return shortUUID(ALPHABET_62, SHORT_UUID_LENGTH);
    }

    /**
     * 生成短uuid
     * 字符集 0-9 a-z
     * @return
     */
    public static String shortUUID36() {
        return shortUUID(ALPHABET_36, SHORT_UUID_LENGTH);
    }

}
