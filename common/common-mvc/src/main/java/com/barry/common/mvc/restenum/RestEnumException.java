package com.barry.common.mvc.restenum;

import org.springframework.core.NestedRuntimeException;

public class RestEnumException extends NestedRuntimeException {
    public RestEnumException(String msg) {
        super(msg);
    }

    public RestEnumException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
