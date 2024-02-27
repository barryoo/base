package com.barry.common.core.exception;

import java.util.stream.Stream;

/**
 * <p>业务错误码定义</p>
 * <ul>
 * <li>该类仅包含预定义业务错误码，自定义错误不允许添加到该类中</li>
 * <li>若需要自定，请自行在所属模块新建 XXXErrorCode.java 类使用</li>
 * </ul>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午1:10:10
 * @see ErrorCode
 */
public enum BusinessErrorCode implements ErrorCode {
    /**
     * 操作错误
     */
    BIZ_BUSINESS_ERROR(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0000", ""),
    /**
     * 无效的参数
     */
    BIZ_INVALID_PARAM_ERROR(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0001", "invalid param"),
    /**
     * 必要参数缺失
     */
    BIZ_LACK_NECESSARY_PARAM_ERROR(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0002", "lack necessary param"),
    /**
     * 重复的 key
     */
    BIZ_DUPLICATE_KEY_ERROR(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0003", "duplicate key"),
    /**
     * API 路径找不到
     */
    BIZ_PATH_NOT_FOUND(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0004", "can't found path"),
    /**
     * 数据修改失败
     */
    BIZ_MODIFY_FAIL(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0005", "modify fail"),
    /**
     * http接口不支持该请求方法
     */
    BIZ_METHOD_NOT_SUPPORTED(ErrorMessageConsts.BIZ_ERROR_CODE_PREFIX + "0006", "method not supported"),
    ;

    private final String code;
    private final String defaultMessage;

    BusinessErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    /**
     * <p>通过 code 获取对应 BusinessErrorCode 实例</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 14:30:37 GMT+8
     */
    public static BusinessErrorCode getByCode(String code) {
        return Stream.of(values())
                .filter(e -> e.getCode().equals(code))
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
