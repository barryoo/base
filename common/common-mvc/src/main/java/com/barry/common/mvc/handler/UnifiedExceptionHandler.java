package com.barry.common.mvc.handler;

import com.barry.common.core.bean.ApiResult;
import com.barry.common.core.exception.ApplicationCheckException;
import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.BusinessCheckException;
import com.barry.common.core.exception.BusinessErrorCode;
import com.barry.common.core.exception.BusinessException;
import com.barry.common.core.exception.ErrorCode;
import com.barry.common.core.exception.RequestClientException;
import com.barry.common.core.exception.SystemErrorCode;
import com.barry.common.core.util.StringUtils;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static com.barry.common.core.exception.ErrorMessageConsts.BIZ_ERROR_LOG_MESSAGE_TEMPLATE;
import static com.barry.common.core.exception.ErrorMessageConsts.SYS_ERROR_LOG_MESSAGE_TEMPLATE;


/**
 * <p>统一异常处理</p>
 *
 * @author chenpeng
 * Create time 2018年11月30日 下午4:41:27
 * @see ErrorCode
 * @see com.barry.common.core.exception.BusinessException
 */
@ControllerAdvice
public class UnifiedExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedExceptionHandler.class);

    private static final String EXCEPTION_MESSAGE_SEPARATOR = ":";

    /**
     * 业务运行时异常
     * @param e
     * @return
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        logWarn(e.getErrorCode(), e, false);
        return returnBusinessException(e);
    }

    /**
     * 业务检出异常
     * @param e
     * @return
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(BusinessCheckException.class)
    public ApiResult<Void> handleBusinessCheckException(BusinessCheckException e) {
        logError(e.getErrorCode(), e, false);
        return returnBusinessCheckException(e);
    }

    /**
     * <p>系统异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:50:45 GMT+8
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public ApiResult<Void> handleApplicationException(ApplicationException e) {
        ErrorCode errorCode = e.getErrorCode();
        logError(errorCode, e, true);
        return ApiResult.error(errorCode.getCode(), e.getClass().getName(), errorCode.getDefaultMessage());
    }

    /**
     * <p>系统异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:50:45 GMT+8
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(ApplicationCheckException.class)
    public ApiResult<Void> handleApplicationCheckException(ApplicationCheckException e) {
        ErrorCode errorCode = e.getErrorCode();
        logError(errorCode, e, true);
        return ApiResult.error(errorCode.getCode(), e.getClass().getName(), errorCode.getDefaultMessage());
    }

    /**
     * <p>最终兜底异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:50:22 GMT+8
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ApiResult<Void> handleThrowable(Throwable e) {

        ErrorCode errorCode;

        if (e instanceof OutOfMemoryError) {
            errorCode = SystemErrorCode.SYS_SYSTEM_OOM_ERROR;
        } else if (e instanceof StackOverflowError) {
            errorCode = SystemErrorCode.SYS_SYSTEM_SOF_ERROR;
        } else {
            errorCode = SystemErrorCode.SYS_SYSTEM_ERROR;
        }

        logError(errorCode, e, true);
        return ApiResult.error(errorCode.getCode(), e.getClass().getName(), String.format("%s-%s-%s", errorCode.getDefaultMessage(),
                e.getClass().getSimpleName(), e.getMessage()));
    }

    /**
     * <p>Duplicate key 异常处理</p>
     *
     * @author chenpeng
     * Create at June 10, 2019 at 18:27:05 GMT+8
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        Throwable rootCause = Throwables.getRootCause(e);
        ErrorCode errorCode = ErrorCode.create(BusinessErrorCode.BIZ_DUPLICATE_KEY_ERROR.getCode(),
                BusinessErrorCode.BIZ_DUPLICATE_KEY_ERROR.getDefaultMessage());
        BusinessException be = new BusinessException(errorCode, rootCause, rootCause.getMessage());
        logError(errorCode, be, false);
        return returnBusinessException(be);
    }

    /**
     * <p>参数异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:51:01 GMT+8
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = null;
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            StringBuilder messageBuilder = new StringBuilder();
            constraintViolations.forEach(cv -> messageBuilder.append(cv.getMessage()).append(","));
            errorMessage = messageBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }
        }
        ErrorCode errorCode = ErrorCode.create(BusinessErrorCode.BIZ_INVALID_PARAM_ERROR.getCode(),
                BusinessErrorCode.BIZ_INVALID_PARAM_ERROR.getDefaultMessage());
        logWarnWithoutStackTrace(errorCode, ex, false);
        return returnBusinessException(new BusinessException(errorCode, ex, errorMessage));
    }

    /**
     * <p>方法参数异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:51:20 GMT+8
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResult<Void> handleMethodArgumentNotValidException(Exception ex) {
        String errorMessage = null;
        List<ObjectError> objectErrors = null;
        if (ex instanceof MethodArgumentNotValidException) {
            objectErrors = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
        } else if (ex instanceof BindException) {
            objectErrors = ((BindException) ex).getBindingResult().getAllErrors();
        }
        if (!CollectionUtils.isEmpty(objectErrors)) {
            StringBuilder messageBuilder = new StringBuilder();
            objectErrors.forEach(o -> {
                if (o instanceof FieldError) {
                    messageBuilder.append(((FieldError) o).getField());
                }
                messageBuilder.append(o.getDefaultMessage()).append(",");
            });
            errorMessage = messageBuilder.toString();
            if (errorMessage.length() > 1) {
                errorMessage = errorMessage.substring(0, errorMessage.length() - 1);
            }
        }
        ErrorCode errorCode = ErrorCode.create(BusinessErrorCode.BIZ_INVALID_PARAM_ERROR.getCode(),
                BusinessErrorCode.BIZ_INVALID_PARAM_ERROR.getDefaultMessage());
        logWarnWithoutStackTrace(errorCode, ex, false);
        return returnBusinessException(new BusinessException(errorCode, ex, errorMessage));
    }

    /**
     * <p>Servlet 注解的参数异常处理</p>
     *
     * @author chenpeng
     * Create at March 7, 2019 at 13:51:40 GMT+8
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String msg = e.getParameterName() + " is required!";
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, msg));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            MissingServletRequestPartException.class,
    })
    public ApiResult<Void> handleHttpBadRequestException(Exception e, HttpServletRequest req) {
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler({
            RequestClientException.class
    })
    public ApiResult<Void> handleRequestClientException(Exception e, HttpServletRequest req) {
        RequestClientException ex = (RequestClientException) e;
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, ex, false);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, ex, ex.getCustomMessage()));
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest req) {
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ApiResult<Void> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e, HttpServletRequest req) {
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }


    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
        logWarnWithoutStackTrace(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler({
            MissingPathVariableException.class,
            ConversionNotSupportedException.class,
            HttpMessageNotWritableException.class,
    })
    public ApiResult<Void> handleMissingPathVariableException(Exception e, HttpServletRequest req) {
        logError(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ApiResult<Void> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e, HttpServletRequest req) {
        logError(BusinessErrorCode.BIZ_BUSINESS_ERROR, e, false);
        Throwable rootCause = Throwables.getRootCause(e);
        return returnBusinessException(new BusinessException(BusinessErrorCode.BIZ_BUSINESS_ERROR, rootCause, rootCause.getMessage()));
    }

    private void logError(ErrorCode errorCode, Throwable t, boolean isSystemError) {
        String logTemplate = isSystemError ? SYS_ERROR_LOG_MESSAGE_TEMPLATE : BIZ_ERROR_LOG_MESSAGE_TEMPLATE;
        LOGGER.error(String.format(logTemplate, errorCode.getCode(), t.getMessage()), t);
    }

    private void logWarn(ErrorCode errorCode, Throwable t, boolean isSystemError) {
        String logTemplate = isSystemError ? SYS_ERROR_LOG_MESSAGE_TEMPLATE : BIZ_ERROR_LOG_MESSAGE_TEMPLATE;
        LOGGER.warn(String.format(logTemplate, errorCode.getCode(), t.getMessage()), t);
    }

    private void logWarnWithoutStackTrace(ErrorCode errorCode, Throwable t, boolean isSystemError) {
        String logTemplate = isSystemError ? SYS_ERROR_LOG_MESSAGE_TEMPLATE : BIZ_ERROR_LOG_MESSAGE_TEMPLATE;
        LOGGER.warn(String.format(logTemplate, errorCode.getCode(), t.getMessage()));
    }

    private ApiResult<Void> returnBusinessException(BusinessException e){
        ErrorCode errorCode = e.getErrorCode();
        String customMessage = e.getCustomMessage();
        String separator = StringUtils.isAnyBlank(errorCode.getDefaultMessage(), customMessage)? StringUtils.EMPTY:EXCEPTION_MESSAGE_SEPARATOR;
        return ApiResult.error(errorCode.getCode(), e.getClass().getName(), errorCode.getDefaultMessage() + separator + customMessage);
    }

    private ApiResult<Void> returnBusinessCheckException(BusinessCheckException e){
        ErrorCode errorCode = e.getErrorCode();
        String customMessage = e.getCustomMessage();
        String separator = StringUtils.isAnyBlank(errorCode.getDefaultMessage(), customMessage)? StringUtils.EMPTY:EXCEPTION_MESSAGE_SEPARATOR;
        return ApiResult.error(errorCode.getCode(), e.getClass().getName(), errorCode.getDefaultMessage() + separator + customMessage);
    }

}
