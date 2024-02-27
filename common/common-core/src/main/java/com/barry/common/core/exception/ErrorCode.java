package com.barry.common.core.exception;

/**
 * <p>错误码接口</p>
 * 自定错误码类/枚举应该实现该接口，以达成统一约定:
 * <ul>
 *     <li>{@link ErrorCode#getCode()} 获取错误码</li>
 *     <li>{@link ErrorCode#getDefaultMessage()} 获取默认错误消息</li>
 * </ul>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午1:57:41
 * @see SystemErrorCode
 */
public interface ErrorCode {

    /**
     * <p>根据 code 和 默认 message 创建一个 ErrorCode 实例（运行时）</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 14:32:24 GMT+8
     */
    static ErrorCode create(String code, String message) {
        return new ErrorCode() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getDefaultMessage() {
                return message;
            }
        };
    }

    /**
     * <p>获取错误码</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 14:31:38 GMT+8
     */
    String getCode();

    /**
     * <p>获取默认消息</p>
     *
     * @author chenpeng
     * Create at May 29, 2019 at 14:31:53 GMT+8
     */
    String getDefaultMessage();
}
