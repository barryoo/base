package com.barry.common.core.exception;

/**
 * <p>错误消息常量</p>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午4:48:10
 */
public final class ErrorMessageConsts {

    public static final String EXCEPTION_DEFAULT_CUSTOM_MESSAGE = "";
    public static final String Exception_DEFAULT_DETAIL_MESSAGE = "";

    public static final String SYS_ERROR_CODE_PREFIX = "SYS-";
    public static final String BIZ_ERROR_CODE_PREFIX = "BIZ-";

    public static final String BIZ_EXCEPTION_MESSAGE_TEMPLATE = "BusinessException{errorCode:%s, " + "defaultMessage:%s, customMessage:%s, detailMessage: %s}";
    public static final String BIZ_EXTENDS_EXCEPTION_MESSAGE_TEMPLATE = "%s{errorCode:%s, " + "defaultMessage:%s, customMessage:%s, detailMessage: %s}";
    public static final String SYS_EXCEPTION_MESSAGE_TEMPLATE = "ApplicationException{errorCode:%s, " + "defaultMessage:%s, customMessage:%s, detailMessage: %s}";

    public static final String BIZ_ERROR_LOG_MESSAGE_TEMPLATE = "%s --- BIZ >> business error, ERROR：%s";
    public static final String SYS_ERROR_LOG_MESSAGE_TEMPLATE = "%s --- SYS >> sys error，ERROR：%s";

    private ErrorMessageConsts() {
    }
}
