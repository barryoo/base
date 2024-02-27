package com.barry.common.core.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>业务逻辑异常</p>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午2:16:37
 * @see ErrorCode
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -5548103768989678861L;

    private final ErrorCode errorCode;
    private final String customMessage;

    public BusinessException(String customMessage) {
        this(BusinessErrorCode.BIZ_BUSINESS_ERROR, customMessage);
    }

    public BusinessException(String customMessage, Throwable cause) {
        this(BusinessErrorCode.BIZ_BUSINESS_ERROR, cause, customMessage);
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause, null);
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        this(errorCode, null, customMessage);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause, String customMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.customMessage = StringUtils.isBlank(customMessage)
                ? ErrorMessageConsts.EXCEPTION_DEFAULT_CUSTOM_MESSAGE : customMessage;
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        return String.format(ErrorMessageConsts.BIZ_EXCEPTION_MESSAGE_TEMPLATE,
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                customMessage,
                cause != null ? cause.getMessage() : ErrorMessageConsts.Exception_DEFAULT_DETAIL_MESSAGE);
    }
}
