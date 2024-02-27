package com.barry.common.core.exception;

import lombok.Getter;

/**
 * @author barry chen
 * @date 2023/2/9 14:58
 */
@Getter
public class RequestClientException extends BusinessException{

    public RequestClientException(String customMessage) {
        super(customMessage);
    }

    public RequestClientException(String customMessage, Throwable cause) {
        super(customMessage, cause);
    }

    public RequestClientException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RequestClientException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public RequestClientException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public RequestClientException(ErrorCode errorCode, Throwable cause, String customMessage) {
        super(errorCode, cause, customMessage);
    }

    @Override
    public String getMessage() {
        Throwable cause = getCause();
        return String.format(ErrorMessageConsts.BIZ_EXTENDS_EXCEPTION_MESSAGE_TEMPLATE,
                this.getClass().getSimpleName(),
                super.getErrorCode().getCode(),
                super.getErrorCode().getDefaultMessage(),
                super.getCustomMessage(),
                cause != null ? cause.getMessage() : ErrorMessageConsts.Exception_DEFAULT_DETAIL_MESSAGE);
    }

}
