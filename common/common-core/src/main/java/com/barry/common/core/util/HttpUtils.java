package com.barry.common.core.util;

import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.SystemErrorCode;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author chenpeng
 * @date 2019-03-21
 * @time 10:42
 */
@Slf4j
public final class HttpUtils {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final String DEFAULT_CLIENT = "default";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Map<String, OkHttpClient> OK_HTTP_CLIENT_MAP = new HashMap<>(16);

    static {
        OK_HTTP_CLIENT_MAP.put(DEFAULT_CLIENT, produceClient(2L, 5L, 5L, 10));
    }

    private HttpUtils() {
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @return 返回结果
     * @throws IOException 错误
     */
    public static String get(String url) throws IOException {
        OkHttpClient okHttpClient = getClient(url);
        Request request = buildRequest(url);
        Response response = okHttpClient.newCall(request).execute();
        return responseToString(response);
    }

    /**
     * get请求 带token
     *
     * @param url       请求地址
     * @param headerMap headerMap
     * @return 返回结果
     * @throws IOException 错误
     */
    public static String get(String url, Map<String, String> headerMap) throws IOException {
        OkHttpClient okHttpClient = getClient(url);
        Request request = buildRequest(url, headerMap);
        Response response = okHttpClient.newCall(request).execute();
        return responseToString(response);
    }

    /**
     * get异步请求
     *
     * @param url      请求地址
     * @param callback 回调
     */
    public static void get(String url, Callback callback) {
        OkHttpClient okHttpClient = getClient(url);
        Request request = buildRequest(url);
        if (callback == null) {
            callback = getDefaultCallback();
        }
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * post 表单
     *
     * @param url    请求地址
     * @param reqObj 请求参数
     * @return 返回结果
     * @throws IOException 异常
     */
    public static String postForm(String url, Object reqObj) throws IOException {
        OkHttpClient okHttpClient = getClient(url);
        return postForm(okHttpClient, url, reqObj);
    }


    /**
     * post 表单 带token
     *
     * @param url       请求地址
     * @param reqObj    请求参数
     * @param headerMap header
     * @return 返回结果
     * @throws IOException 异常
     */
    public static String postForm(String url, Object reqObj, Map<String, String> headerMap) throws IOException {
        OkHttpClient okHttpClient = getClient(url);
        return postForm(okHttpClient, url, reqObj, headerMap);
    }

    public static String postForm(OkHttpClient okHttpClient, String url, Object reqObj) throws IOException {
        if (okHttpClient == null) {
            okHttpClient = getClient(url);
        }
        RequestBody formBody = buildFormBody(reqObj);
        Request request = buildRequest(url, formBody);
        Response response = okHttpClient.newCall(request).execute();
        return responseToString(response);
    }

    public static String postForm(OkHttpClient okHttpClient, String url, Object reqObj, Map<String, String> headerMap) throws IOException {
        if (okHttpClient == null) {
            okHttpClient = getClient(url);
        }
        RequestBody formBody = buildFormBody(reqObj);
        Request request = buildRequest(url, formBody, headerMap);
        Response response = okHttpClient.newCall(request).execute();
        return responseToString(response);
    }

    public static String responseToString(Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                String result = null;
                if (response.body() != null) {
                    result = response.body().string();
                }
                return result;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } finally {
            if (response.body() != null) {
                response.body().close();
            }
            response.close();
        }
    }

    /**
     * 异步 post 表单 提交
     *
     * @param url      请求地址
     * @param reqObj   请求参数
     * @param callback 回调处理
     */
    public static void postForm(String url, Object reqObj, Callback callback) {
        OkHttpClient okHttpClient = getClient(url);
        FormBody formBody = buildFormBody(reqObj);
        Request request = buildRequest(url, formBody);
        if (callback == null) {
            callback = getDefaultCallback();
        }
        okHttpClient.newCall(request).enqueue(callback);

    }

    /**
     * post json
     *
     * @param url    请求地址
     * @param reqObj 请求参数
     * @return 返回字符串
     * @throws IOException 异常
     */
    public static String postJson(String url, Object reqObj) throws IOException {
        OkHttpClient okHttpClient = getClient(url);
        return postJson(okHttpClient, url, reqObj);
    }

    public static String postJson(OkHttpClient okHttpClient, String url, Object reqObj) throws IOException {
        if (okHttpClient == null) {
            okHttpClient = getClient(url);
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSON_MAPPER.toJson(reqObj));
        Response response = okHttpClient.newCall(buildRequest(url, body)).execute();
        try {
            if (response.isSuccessful()) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } finally {
            if (response.body() != null) {
                response.body().close();
            }
            response.close();
        }
    }

    /**
     * 异步提交
     *
     * @param url      请求地址
     * @param reqObj   请求参数
     * @param callback 回调处理
     */
    public static void postJson(String url, Object reqObj, Callback callback) {
        OkHttpClient okHttpClient = getClient(url);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, JSON_MAPPER.toJson(reqObj));
        if (callback == null) {
            callback = getDefaultCallback();
        }
        okHttpClient.newCall(buildRequest(url, body)).enqueue(callback);
    }

    public static Request buildRequest(String url, RequestBody body) {
        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static Request buildRequest(String url, RequestBody body, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            builder = builder.addHeader(key, headerMap.get(key));
        }
        return builder
                .url(url)
                .post(body)
                .build();
    }

    public static Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }


    public static Request buildRequest(String url, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            builder = builder.addHeader(key, headerMap.get(key));
        }
        return builder
                .url(url)
                .build();
    }

    public static FormBody buildFormBody(Object reqObj) {
        FormBody.Builder builder = new FormBody.Builder();

        Map<String, Object> mapObj = beanToMap(reqObj);
        for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
            if (entry.getValue() != null) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }

        return builder.build();
    }

    private static OkHttpClient getClient(String url) {

        String hostName = getHost(url);
        if (OK_HTTP_CLIENT_MAP.containsKey(hostName)) {
            return OK_HTTP_CLIENT_MAP.get(hostName);
        } else {
            return OK_HTTP_CLIENT_MAP.get(DEFAULT_CLIENT);
        }
    }

    /**
     * 获取默认callback处理
     *
     * @return
     */
    private static Callback getDefaultCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    response.close();
                } else {
                    log.error("请求：{} 失败", call.request().url());
                }
            }
        };
    }

    /**
     * 获取 url 的 host
     *
     * @param url 请求地址
     * @return host
     */
    public static String getHost(String url) {
        URI uri = URI.create(url);
        String hostName = uri.getHost();
        if (StringUtils.isEmpty(hostName)) {
            hostName = DEFAULT_CLIENT;
        }
        return hostName;
    }

    /**
     * 构造 httpClient
     *
     * @param connectTime        连接超时（秒）
     * @param writeTime          写入超时（秒）
     * @param readTime           读取超时（秒）
     * @param maxRequestsPerHost 正在执行的总任务数及相同host下正在执行的任务数小于阈值时，直接执行任务
     * @return OkHttpClient
     */
    public static OkHttpClient produceClient(Long connectTime, Long writeTime, Long readTime,
                                             Integer maxRequestsPerHost) {
        return produceClient(connectTime, writeTime, readTime, maxRequestsPerHost, null);
    }

    public static OkHttpClient produceClient(Long connectTime, Long writeTime, Long readTime,
                                             Integer maxRequestsPerHost, List<Interceptor> interceptors) {

        OkHttpClient.Builder builder = builder(connectTime, writeTime, readTime);
        if (interceptors != null && interceptors.size() > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        OkHttpClient client = builder.build();
        if (maxRequestsPerHost != null && maxRequestsPerHost > 0) {
            client.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
        }
        return client;
    }

    /**
     * 预设 httpClientMap 超时时间
     *
     * @param url
     * @param connectTime
     * @param writeTime
     * @param readTime
     * @param maxRequestsPerHost
     */
    public static void putHttpClient(String url, Long connectTime, Long writeTime, Long readTime,
                                     Integer maxRequestsPerHost) {
        String hostName = getHost(url);
        synchronized (hostName.intern()) {
            OK_HTTP_CLIENT_MAP.put(hostName, produceClient(connectTime, writeTime, readTime, maxRequestsPerHost));
        }
    }

    /**
     * 设置url 对应的客户端
     *
     * @param url          url
     * @param okHttpClient ok http client
     */
    public static void putHttpClient(String url, OkHttpClient okHttpClient) {
        String hostName = getHost(url);
        synchronized (hostName.intern()) {
            OK_HTTP_CLIENT_MAP.put(hostName, okHttpClient);
        }
    }

    /**
     * 获取默认的客户端
     *
     * @return ok http client
     */
    public static OkHttpClient getDefaultClient() {
        return OK_HTTP_CLIENT_MAP.get(DEFAULT_CLIENT);
    }

    public static OkHttpClient.Builder builder(Long connectTime, Long writeTime, Long readTime) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //连接超时
        if (connectTime != null && connectTime.intValue() > 0) {
            builder.connectTimeout(connectTime, TimeUnit.SECONDS);
        }
        //连接超时
        if (writeTime != null && writeTime.intValue() > 0) {
            builder.writeTimeout(writeTime, TimeUnit.SECONDS);
        }
        //连接超时
        if (readTime != null && readTime.intValue() > 0) {
            builder.readTimeout(readTime, TimeUnit.SECONDS);
        }
        return builder;
    }

    public static Map<String, Object> beanToMap(Object bean) {
        return JSON_MAPPER.fromJson(JSON_MAPPER.toJson(bean), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 通过Http Url下载文件至本地
     *
     * @param url      httpUrl
     * @param destDir  本地文件路径
     * @param fileName 本地文件名
     * @return 下载到的文件
     * @throws Exception Exception
     */
    public static File downloadFile(String url, String destDir, String fileName) {
        File file = new File(destDir + File.separatorChar + fileName);
        Request request = new Request.Builder().url(url).build();
        return copyFile(request, file, url);
    }

    /**
     * 通过Http Url下载文件至本地
     *
     * @param url      httpUrl
     * @param destDir  本地文件路径
     * @param fileName 本地文件名
     * @return 下载到的文件
     * @throws Exception Exception
     */
    public static File downloadFileByCookie(String url, String destDir, String fileName, String cookie) {
        File file = new File(destDir + File.separatorChar + fileName);
        Request request = new Request.Builder().
                url(url).
                addHeader("Cookie", cookie).
                build();
        return copyFile(request, file, url);
    }

    /**
     * 将Http响应存入文件
     *
     * @param request request
     * @param file    file
     * @param url     url
     * @return file
     */
    private static File copyFile(Request request, File file, String url) {
        Response response;
        try {
            response = getClient(url).newCall(request).execute();
        } catch (IOException e) {
            throw new ApplicationException(SystemErrorCode.SYS_HTTP_CLIENT_ERROR, e, "Create http connection with error");
        }
        if (response.code() != 200) {
            throw new ApplicationException(SystemErrorCode.SYS_HTTP_CLIENT_ERROR, "Response code is " + response.code());
        }
        if (Objects.nonNull(response.body())) {
            try (BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                 BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));) {
                IOUtils.copy(bis, bos);
                return file;
            } catch (IOException e) {
                throw new ApplicationException(SystemErrorCode.SYS_HTTP_DOWNLOAD_ERROR, e, "Copy file from http with error");
            }
        } else {
            throw new ApplicationException(SystemErrorCode.SYS_HTTP_CLIENT_ERROR, "Http response body is empty.");
        }
    }
}
