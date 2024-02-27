package com.barry.common.core.util;

import com.barry.common.core.enums.EnvEnum;

/**
 * 当前环境相关工具
 *
 * @author barry chen
 * @date 2021/3/30 11:15
 */
public final class EnvUtils {

    private EnvUtils() {
    }

    /**
     * 判断当前环境是不是local环境
     */
    public static boolean isLocal(String cur) {
        return isProfile(cur, EnvEnum.LOCAL);
    }

    /**
     * 判断当前环境是不是dev环境.
     *
     * @param cur
     * @return
     */
    public static Boolean isDev(String cur) {
        return isProfile(cur, EnvEnum.DEV);
    }

    /**
     * 判断当前环境是test环境
     *
     * @param cur
     * @return
     */
    public static Boolean isTest(String cur) {
        return isProfile(cur, EnvEnum.TEST);
    }

    /**
     * 判断当前环境是prod环境
     *
     * @param cur
     * @return
     */
    public static Boolean isProd(String cur) {
        return isProfile(cur, EnvEnum.PROD);
    }

    public static Boolean isProfile(String cur, EnvEnum envEnum) {
        return StringUtils.equalsAnyIgnoreCase(cur, envEnum.getName(), envEnum.getFullName());
    }
}
