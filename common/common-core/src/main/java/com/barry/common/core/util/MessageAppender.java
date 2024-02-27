package com.barry.common.core.util;

/**
 * 支持自动控制tab缩进的字符串拼接器
 */
public class MessageAppender {

    private final StringBuilder value;
    private int preTabNums;

    public MessageAppender(String value) {
        this.value = new StringBuilder(value);
    }


    public MessageAppender append(String str) {
        value.append(str);
        return this;
    }

    /**
     * 新增一行时继承上一行的缩进
     * @param str
     * @return
     */
    public MessageAppender appendLn(String str) {
        value.append(str).append("\n");
        for (int i = 0; i < preTabNums; i++) {
            value.append("\t");
        }
        return this;
    }
    public MessageAppender appendLn() {
        value.append("\n");
        for (int i = 0; i < preTabNums; i++) {
            value.append("\t");
        }
        return this;
    }

    /**
     * 在换行之前调用表示当前行缩进
     * 在换行之后调用表示下一行缩进
     * @return
     */
    public MessageAppender openTab() {
        int offset = value.lastIndexOf("\n");
        value.insert(offset+1, "\t");
        preTabNums += 1;
        return this;
    }

    public MessageAppender closeTab() {
        int offset = value.lastIndexOf("\n");
        value.delete(offset+1, offset+2);
        preTabNums = preTabNums==0?0:preTabNums-1;
        return this;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
