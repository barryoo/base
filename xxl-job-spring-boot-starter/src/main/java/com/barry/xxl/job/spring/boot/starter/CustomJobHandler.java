package com.barry.xxl.job.spring.boot.starter;

import com.barry.common.core.util.StringUtils;
import com.barry.common.spring.util.JsonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class CustomJobHandler extends MethodJobHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodJobHandler.class);
    private final Object target;
    private final Method method;


    public CustomJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        super(target, method, initMethod, destroyMethod);
        this.target = target;
        this.method = method;
    }

    /**
     * <p>处理参数类型</p>
     *
     * @param arg 字符串参数
     * @author chenpeng
     * Create at January 24, 2019 at 11:58:15 GMT+8
     */
    protected Object resolveParam(String arg) {
        if (StringUtils.isBlank(arg)) {
            return null;
        }

        Class<?> paramType = method.getParameterTypes()[0];

        if (paramType == String.class) {
            return arg;
        }

        if (paramType == Void.class) {
            return null;
        }
        return JsonUtils.fromJson(arg, method.getParameterTypes()[0]);
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        ReturnT<String> result = ReturnT.SUCCESS;

        try {
            XxlJobLogger.log("#### job start >>>>>>>");
            method.invoke(target, new Object[]{resolveParam(param)});
            XxlJobLogger.log("#### job end  <<<<<<<<");
        } catch (Exception ex) {
            result = new ReturnT<>();
            result.setCode(ReturnT.FAIL_CODE);
            result.setMsg(ex.getMessage());
            LOGGER.error("#### job 执行失败", ex);
        }

        return result;
    }
}
