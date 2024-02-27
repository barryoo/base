package com.barry.common.core.exception;

import java.util.stream.Stream;

/**
 * <p>系统错误码定义</p>
 * <ul>
 * <li>该类仅包含预定义系统错误码，自定义错误不允许添加到该类中</li>
 * <li>若需要自定，请自行在所属模块新建 XXXErrorCode.java 类使用</li>
 * </ul>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午1:10:10
 * @see ErrorCode
 */
public enum SystemErrorCode implements ErrorCode {

    /**
     * 系统内部错误
     */
    SYS_SYSTEM_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0000", "Internal system error"),
    /**
     * 系统堆内存不足
     */
    SYS_SYSTEM_OOM_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0001", "Insufficient heap memory"),
    /**
     * 系统栈溢出
     */
    SYS_SYSTEM_SOF_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0002", "stack overflow"),
    /*
     * Feign 调用结果解码错误
     */
    SYS_SYSTEM_FEIGN_RESULT_DECODE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0003", "Feign decode result error"),
    /**
     * package 路径查找错误
     */
    SYS_SYSTEM_PKG_RESOLVE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0004", "package path can't be found"),
    /**
     * MQ 消息发送失败
     */
    SYS_MQ_SEND_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0005", "MQ send message fail"),
    /**
     * MQ 消费失败
     */
    SYS_MQ_CONSUME_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0006", "MQ consume message fail"),

    /**
     * Id Worker 初始化失败
     */
    SYS_ID_WORKER_INIT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "007", "init id worker fail"),

    /**
     * http工具异常 0100 - 0149
     */
    SYS_HTTP_CLIENT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0100", "http client error"),
    SYS_HTTP_DOWNLOAD_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0101", "http download file error"),

    /**
     * 邮件工具异常 0150 - 0199
     */
    SYS_MAIL_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0150", "mail client error"),
    SYS_MAIL_ATTACHMENT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0151", "mail attachment error"),
    SYS_MAIL_CONTENT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0152", "mail content error"),

    /**
     * ftp工具异常 0200 - 0249
     */
    SYS_FTP_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0200", "ftp client error"),
    SYS_FTP_CONNECT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0201", "ftp connect error" ),
    SYS_FTP_LIST_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0202", "ftp list file error" ),
    SYS_FTP_DOWNLOAD_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0203", "ftp download file error" ),
    SYS_FTP_DISCONNECT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0204", "ftp disconnect error" ),
    SYS_FTP_CREATE_FOLDER_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0205", "ftp create folder error" ),
    SYS_FTP_UPLOAD_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0206", "ftp upload file error" ),

    /**
     * sftp工具异常
     */
    SYS_SFTP_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0250", "sftp client error"),
    SYS_SFTP_CONNECT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0251", "sftp connect error"),
    SYS_SFTP_DISCONNECT_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0252", "sftp disconnect error"),
    SYS_SFTP_LIST_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0253", "sftp list file error"),
    SYS_SFTP_UPLOAD_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0254", "sftp upload file error"),
    SYS_SFTP_DOWNLOAD_FILE_ERROR(ErrorMessageConsts.SYS_ERROR_CODE_PREFIX + "0255", "sftp download file error"),

    ;
    private final String code;
    private final String defaultMessage;

    SystemErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    /**
     * <p>通过 code 获得对应 SystemErrorCode 实例</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 14:33:32 GMT+8
     */
    public static SystemErrorCode getByCode(String code) {
        return Stream.of(values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
