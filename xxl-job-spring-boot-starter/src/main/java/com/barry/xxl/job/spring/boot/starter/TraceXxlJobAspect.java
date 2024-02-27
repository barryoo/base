package com.barry.xxl.job.spring.boot.starter;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 把xxlJob加入到sleuth调用链中
 *
 * @date 2021/12/28 3:43 下午
 */
@Component
@Aspect
@ConditionalOnClass(Tracing.class)
public class TraceXxlJobAspect {

    private final Tracing tracing;

    public TraceXxlJobAspect(Tracing tracing) {
        this.tracing = tracing;
    }

    private static final String CLASS_KEY = "class";

    private static final String METHOD_KEY = "method";


    @Around("execution (@com.xxl.job.core.handler.annotation.XxlJob  * *.*(..))")
    public Object traceBackgroundThread(final ProceedingJoinPoint pjp) throws Throwable {

        String jobHandlerName = null;
        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            Method targetMethod = ((MethodSignature) signature).getMethod();
            if (targetMethod.isAnnotationPresent(XxlJob.class)) {
                XxlJob annotation = targetMethod.getAnnotation(XxlJob.class);
                jobHandlerName = annotation.value();
            }
        }
        if (jobHandlerName == null) {
            return pjp.proceed();
        }

        Tracer tracer = this.tracing.tracer();
        //清除scop中原span
        tracer.withSpanInScope(null);
        String spanName = this.toLowerHyphen(jobHandlerName);
        Span span = tracer.newTrace().name(spanName);
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span.start())) {
            span.tag(CLASS_KEY, pjp.getTarget().getClass().getSimpleName());
            span.tag(METHOD_KEY, pjp.getSignature().getName());
            return pjp.proceed();
        } catch (Throwable ex) {
            String message = ex.getMessage() == null ? ex.getClass().getSimpleName()
                    : ex.getMessage();
            span.tag("error", message);

            throw ex;
        } finally {
            span.finish();
        }
    }

    private String shorten(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        int maxLength = Math.min(name.length(), 50);
        return name.substring(0, maxLength);
    }

    private String toLowerHyphen(String name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(c));
            }
            else {
                result.append(c);
            }
        }
        return this.shorten(result.toString());
    }
}
