package com.barry.common.core.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static com.barry.common.core.exception.ErrorMessageConsts.SYS_EXCEPTION_MESSAGE_TEMPLATE;

/**
 * <p>系统异常(check exception)</p>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午2:16:37
 * @see ErrorCode
 */
@Getter
public class ApplicationCheckException extends Exception {

    private static final long serialVersionUID = 5852486390437353773L;

    private final ErrorCode errorCode;
    private final String customMessage;

    public ApplicationCheckException(String customMessage) {
        this(SystemErrorCode.SYS_SYSTEM_ERROR, customMessage);
    }

    public ApplicationCheckException(String customMessage, Throwable cause) {
        this(SystemErrorCode.SYS_SYSTEM_ERROR, cause, customMessage);
    }

    public ApplicationCheckException(ErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ApplicationCheckException(ErrorCode errorCode, String customMessage) {
        this(errorCode, null, customMessage);
    }

    public ApplicationCheckException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause, null);
    }

    public ApplicationCheckException(ErrorCode errorCode, Throwable cause, String customMessage) {
        super(cause);
        this.errorCode = errorCode;
        this.customMessage = StringUtils.isBlank(customMessage)
                ? ErrorMessageConsts.EXCEPTION_DEFAULT_CUSTOM_MESSAGE : customMessage;
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
