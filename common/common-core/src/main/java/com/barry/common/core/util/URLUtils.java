package com.barry.common.core.util;

import com.barry.common.core.constants.CommonConst;
import okhttp3.HttpUrl;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author barrychen
 */
public final class URLUtils {

    private URLUtils() {
    }

    public static String format(String url) {
        if (!url.contains(CommonConst.HTTP)) {
            url = url + CommonConst.HTTP_PREFIX;
        }
        return url;
    }

    public static HttpUrl okHttpUrl(String url) {
        return HttpUrl.parse(url);
    }

    public static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

/*    public static String getTypeByName(String url){
        int questionMarkIdx = url.indexOf("?");
        int dotIdx = url.indexOf(".");
        if(dotIdx<0){
            return null;
        }
    }*/

}
