package com.barry.common.spring.util;

import com.barry.common.core.enums.EnvEnum;
import com.barry.common.core.util.EnvUtils;

import java.util.Optional;

/**
 * @author barry chen
 *
 * spring项目, 环境相关工具类
 */
public final class SpringEnvUtils {

    private SpringEnvUtils(){}
    /**
     * 获取当前运行的profile,如果有多个profile,则取第一个
     *
     * @return
     */
    public static String getProfile() {
        return Optional.ofNullable(SpringContextUtils.getApplicationContext())
                .map(c -> c.getEnvironment())
                .map(m -> m.getActiveProfiles())
                .filter(p -> p.length > 0)
                .map(p -> p[0]).orElse(null);
    }

    /**
     * 获取当前运行的所有profile
     *
     * @return
     */
    public static String[] getProfiles() {
        return Optional.ofNullable(SpringContextUtils.getApplicationContext())
                .map(c -> c.getEnvironment())
                .map(m -> m.getActiveProfiles())
                .filter(p -> p.length > 0)
                .orElse(null);
    }

    public static String getProper(String key, String defaultValue) {
        return Optional.ofNullable(SpringContextUtils.getApplicationContext())
                .map(c -> c.getEnvironment())
                .map(m -> m.getProperty(key, defaultValue))
                .orElse(null);
    }

    /**
     * 判断当前spring环境是否是local
     * 如果同时存在多个环境, 则任何一个环境是local,就返回true
     *
     * @return
     */
    public static Boolean isLocal() {
        return isProfile(EnvEnum.LOCAL);
    }

    /**
     * 判断当前spring环境是否是dev
     * 如果同时存在多个环境, 则任何一个环境是dev,就返回true
     *
     * @return
     */
    public static Boolean isDev() {
        return isProfile(EnvEnum.DEV);
    }

    /**
     * 判断当前spring环境是否是test
     * 如果同时存在多个环境, 则任何一个环境是test,就返回true
     *
     * @return
     */
    public static Boolean isTest() {
        return isProfile(EnvEnum.TEST);
    }

    /**
     * 判断当前spring环境是否是prod
     * 如果同时存在多个环境, 则任何一个环境是dev,就返回true
     *
     * @return
     */
    public static Boolean isProd() {
        return isProfile(EnvEnum.PROD);
    }

    /**
     * 判断当前spring环境是否是指定的环境
     * 如果同时存在多个环境, 则任意个匹配 ,就返回true
     *
     * @return
     */
    public static Boolean isProfile(EnvEnum envEnum) {
        String[] curProfiles = SpringEnvUtils.getProfiles();
        for (String cur : curProfiles) {
            if (EnvUtils.isProfile(cur, envEnum)) {
                return true;
            }
        }
        return false;
    }

}
