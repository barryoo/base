package com.barry.common.spring.util;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;

/**
 * 应用启动工具类
 *
 * @author chenpeng
 * Create at December 2, 2018 at 21:18:11 GMT+8
 */
public final class ApplicationStartupUtils {


    // private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupUtils.class);

    private static final String PROP_LOGGING_CONFIG_KEY = "logging.config";
    private static final String PROFILE_VULE_SEPARATOR = ",";

    private ApplicationStartupUtils() {

    }

    /**
     * <p>应用启动方法</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 15:21:53 GMT+8
     */
    public static void startup(Class<?> clazz, String[] args) {
        // 设置系统参数 本机IP
        System.setProperty("local-ip", getLocalIp());
        ApplicationContext context = SpringApplication.run(clazz, args);
    }

    private static String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            return "";
        }
    }
}
