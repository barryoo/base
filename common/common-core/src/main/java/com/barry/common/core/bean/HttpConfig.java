package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HTTP配置
 * @author barryChen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpConfig {
    /**
     * Http host地址
     */
    private String host;
    /**
     * Http 端口
     */
    private int port;
    /**
     * 协议类型
     */
    private String protocol;
    /**
     * Http url
     */
    private String url;
    /**
     * Http编码
     */
    private String httpEncode;
    /**
     * 本地文件路径
     */
    private String localFilePath;
    /**
     * cookie
     */
    private String cookie;
}
