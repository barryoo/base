package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FTP配置
 * @author barryChen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FtpConfig {
    /**
     * FTP host地址
     */
    private String host;
    /**
     * FTP 端口
     */
    private int port;
    /**
     * 登陆用户
     */
    private String username;
    /**
     * 登陆密码
     */
    private String password;
    /**
     * FTP编码
     */
    private String ftpEncode;
    /**
     * 本地文件路径
     */
    private String localFilePath;
    /**
     * FTP文件路径
     */
    private String ftpFilePath;
    /**
     * FTP文件名
     */
    private String ftpFileName;
}
