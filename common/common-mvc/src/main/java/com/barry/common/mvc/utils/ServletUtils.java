package com.barry.common.mvc.utils;

import com.barry.common.core.util.*;
import com.google.common.collect.Lists;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.http.HttpHeaders.*;

/**
 * 客户端工具类
 *
 */
public class ServletUtils {
    private static final String FILENAME = "filename";

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * response file stream for HttpServletResponse
     * - set required response header
     * - set content-type to "application/octet-stream" if {@param contentType} is blank
     *
     * @param response
     * @param file
     * @param fileName
     * @param contentType
     */
    public static void returnFileStream(HttpServletResponse response, File file, String fileName, @Nullable String contentType) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpStatus.OK.value());
        setFileStreamHeader(response, Opt.ofBlankable(fileName).orElse(file.getName()));
        final String finalContentType = StringUtils.trim(contentType);
        Assert.apply(StringUtils.isNotBlank(finalContentType), () -> response.setHeader(CONTENT_TYPE, finalContentType));
        try (OutputStream outputStream = response.getOutputStream()) {
            FileUtils.copyFile(file, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFileStreamHeader(HttpServletResponse response, String fileName) {
        try (ServletServerHttpResponse res = new ServletServerHttpResponse(response)) {
            HttpHeaders headers = res.getHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add(FILENAME, URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
            headers.add(CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
            headers.setAccessControlExposeHeaders(Lists.newArrayList(CONTENT_TYPE, CONTENT_DISPOSITION, FILENAME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.equalsAnyIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringUtils.equalsAnyIgnoreCase(ajax, "json", "xml")) {
            return true;
        }
        return false;
    }
}
