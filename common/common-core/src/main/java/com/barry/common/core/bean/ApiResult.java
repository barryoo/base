package com.barry.common.core.bean;

import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;

import static com.barry.common.core.bean.ApiResult.ResultStatus.ERROR;
import static com.barry.common.core.bean.ApiResult.ResultStatus.SUCCESS;

/**
 * <p>Api 调用结果统一包装类</p>
 *
 * @param <T>
 * @author chenpeng
 * Create time 2018年11月30日 下午5:17:42
 */
@Data
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 5247512550376272642L;

    private static final String SUCCESS_CODE = "0";

    private ResultStatus status;

    private String code;

    private String msg;

    private T data;

    private String exception;

    public ApiResult() {
    }

    public ApiResult(T content) {
        this.status = SUCCESS;
        this.code = SUCCESS_CODE;
        this.data = content;
    }

    public ApiResult(String code, String message) {
        this.status = ERROR;
        this.code = code;
        this.msg = message;
    }

    public static <T> ApiResult<T> success(T content) {
        return new ApiResult<>(content);
    }

    public static ApiResult<Void> success() {
        return new ApiResult<>(null);
    }

    public static <T> ApiResult<T> error(String code, String message) {
        return new ApiResult<>(code, message);
    }

    public static <T> ApiResult<T> error(String code, String exception, String message) {
        ApiResult<T> apiResult = new ApiResult<>(code, message);
        apiResult.setException(exception);
        return apiResult;
    }

    @Transient
    public boolean isSuccess() {
        return SUCCESS == status;
    }

    @Transient
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 业务响应状态
     */
    public enum ResultStatus {
        /**
         * 返回状态：成功
         */
        SUCCESS,
        /**
         * 返回状态：失败
         */
        ERROR
    }
}
