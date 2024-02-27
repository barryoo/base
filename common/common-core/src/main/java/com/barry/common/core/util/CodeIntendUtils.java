package com.barry.common.core.util;

/**
 * @author barry chen
 * @date 2023/5/5 16:19
 */
public class CodeIntendUtils {

    /**
     * 对html代码进行最小化.
     * @param html
     * @return
     */
    public static String unIntendHtml(String html){
        //去掉注释
        html = html.replaceAll("<!--.*?-->", "");
        //去掉标签后与标签前的换行和空格
        html = html
                .replaceAll("[\\n\\s]*([>,/>])[\\n\\s]*", "$1")
                .replaceAll("[\\n\\s]*([<,</])[\\n\\s]*", "$1")
                .trim();
        return html;
    }
}
