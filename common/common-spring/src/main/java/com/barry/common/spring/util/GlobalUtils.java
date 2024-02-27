package com.barry.common.spring.util;

/**
 * @author barry chen
 * @date 2020/12/10 12:19 下午
 */
public final class GlobalUtils {

    private GlobalUtils() {
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static String getTempBaseDir() {
        return SpringEnvUtils.getProper("temp.basedir", "");
    }

}
