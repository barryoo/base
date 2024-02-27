package com.barry.common.core.exception;

/**
 * 重试达到上限异常
 * @author barry chen
 * @date 2023/10/17 17:09
 */
public class RetryExceededLimitException extends BusinessException{

    public RetryExceededLimitException(String customMessage, Throwable cause) {
        super(customMessage, cause);
    }

    public RetryExceededLimitException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RetryExceededLimitException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public RetryExceededLimitException(ErrorCode errorCode, Throwable cause, String customMessage) {
        super(errorCode, cause, customMessage);
    }

    public RetryExceededLimitException(String customMessage) {
        super(customMessage);
    }

    public RetryExceededLimitException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

}
