package com.barry.common.core.bean;

import lombok.*;

/**
 * SFTP配置
 * @author barryChen
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class SftpConfig {
    /**
     * SFTP host地址
     */
    @NonNull
    private String host;
    /**
     * SFTP 端口
     */
    private int port;
    /**
     * 登陆用户
     */
    @NonNull
    private String username;
    /**
     * 登陆密码
     */
    private String password;
    /**
     * 通过privateKeyPath登陆
     */
    private String keyPath;
    /**
     * SFTP编码
     */
    private String sftpEncode;
    /**
     * 本地文件路径
     */
    private String localFilePath;
    /**
     * SFTP文件路径
     */
    private String sftpFilePath;
    /**
     * SFTP文件名
     */
    private String sftpFileName;
}
