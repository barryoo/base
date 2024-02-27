package com.barry.common.core.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static com.barry.common.core.exception.ErrorMessageConsts.*;

/**
 * <p>系统异常</p>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午2:16:37
 * @see ErrorCode
 */
@Getter
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -5548103768989678861L;

    private final ErrorCode errorCode;
    private final String customMessage;

    public ApplicationException(String customMessage) {
        this(SystemErrorCode.SYS_SYSTEM_ERROR, customMessage);
    }

    public ApplicationException(String customMessage, Throwable cause) {
        this(SystemErrorCode.SYS_SYSTEM_ERROR, cause, customMessage);
    }

    public ApplicationException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ApplicationException(ErrorCode errorCode, String customMessage) {
        this(errorCode, null, customMessage);
    }

    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause, null);
    }

    public ApplicationException(ErrorCode errorCode, Throwable cause, String customMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.customMessage = StringUtils.isBlank(customMessage)
                ? EXCEPTION_DEFAULT_CUSTOM_MESSAGE : customMessage;
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        return String.format(SYS_EXCEPTION_MESSAGE_TEMPLATE,
                errorCode.getCode(),
                errorCode.getDefaultMessage(),
                customMessage,
                cause != null ? cause.getMessage() : ErrorMessageConsts.Exception_DEFAULT_DETAIL_MESSAGE);
    }
}
