package com.barry.common.spring.config.feign;

import com.barry.common.core.bean.ApiResult;
import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.BusinessException;
import com.barry.common.core.exception.ErrorCode;
import com.barry.common.core.util.Opt;
import com.barry.common.core.util.StringUtils;
import com.barry.common.spring.util.JsonUtils;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

/**
 * <p>Feign 组件</p>
 *
 * @author chenpeng
 * Create at February 21, 2019 at 16:38:07 GMT+8
 */
public class FeignModuler {

    private static final String STRING_BODY_TYPE_NAME = "String";

    /**
     * 通过正则匹配字符串, 来判断response中是否有error.
     * 当包含了 "status":"ERROR" 时, 认为是错误的响应.
     */
    private static final Pattern hasErrorPattern = Pattern.compile("^.*\"status\"\\s*:\\s*\"ERROR\".*$", Pattern.MULTILINE|Pattern.DOTALL);

    @Nullable
    private static RuntimeException processException(Response response, String bodyText) throws IOException {
        RuntimeException ex = null;
        if (StringUtils.isBlank(bodyText)) {
            return ex;
        }
        if (!hasError(bodyText)) {
            return ex;
        }
        ApiResult<Void> errorResult = JsonUtils.fromJson(bodyText, ApiResult.class);
        if (errorResult == null || StringUtils.isBlank(errorResult.getCode())) {
            return ex;
        }
        String code = errorResult.getCode();
        String message = errorResult.getMsg();
        String exception = errorResult.getException();
        Request req = response.request();
        if (StringUtils.isBlank(exception) || exception.equalsIgnoreCase(BusinessException.class.getName())) {
            return new BusinessException(buildErrorCode(code, message, req));
        } else if (exception.equalsIgnoreCase(ApplicationException.class.getName())) {
            return new ApplicationException(buildErrorCode(code, message, req));
        }

        //实例化真正的Exception
        Class<? extends RuntimeException> exClazz;
        try {
            exClazz = (Class<? extends RuntimeException>) Class.forName(exception);
            ex = (RuntimeException) exClazz.getDeclaredConstructor(String.class)
                    .newInstance(String.format("%s - %s", code, message));
        } catch (Exception e) {
            return new BusinessException(buildErrorCode(code, message, req));
        }
        return ex;
    }

    private static boolean hasError(String bodyText) {
        return hasErrorPattern.matcher(bodyText).matches();
    }

    private static boolean isStringReturnType(Type type) {
        String typeName = type.getTypeName();
        return (STRING_BODY_TYPE_NAME.equalsIgnoreCase(typeName)
                || String.class.getName().equalsIgnoreCase(typeName));
    }

    private static ErrorCode buildErrorCode(String code, String message, Request req) {
        return new ErrorCode() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getDefaultMessage() {
                return String.format("remote call error: url:%s, msg:%s", Opt.ofNullable(req).map(Request::url).orElse("unknown"), message);
            }
        };
    }

    /**
     * 解码器
     */
    static class ResultDecoder implements Decoder {
        @Override
        public Object decode(Response response, Type type) throws IOException, FeignException {
            Response.Body body = response.body();
            String bodyText = Util.toString(body.asReader());
            RuntimeException e = processException(response, bodyText);
            if (e != null) {
                throw e;
            }
            if (isStringReturnType(type)) {
                return bodyText;
            }
            return JsonUtils.fromJson(bodyText, JsonUtils.getJsonMapper().getTypeFactory().constructType(type));
        }

    }

    /**
     * 异常处理解码器
     */
    static class ExceptionDecoder extends ErrorDecoder.Default {
        @SneakyThrows
        @Override
        public Exception decode(String methodKey, Response response) {
            Response.Body body = response.body();
            String bodyText = Util.toString(body.asReader());
            RuntimeException e = processException(response, bodyText);
            if(e!=null){
                return e;
            }
            return super.decode(methodKey, response);
        }
    }

}
