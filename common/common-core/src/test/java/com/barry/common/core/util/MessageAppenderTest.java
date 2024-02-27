package com.barry.common.core.util;

import org.junit.Test;

public class MessageAppenderTest {

    @Test
    public void messageAppenderTest() {
        MessageAppender messageAppender = new MessageAppender("message appender");
        messageAppender.appendLn()
                .openTab().appendLn("1 message appender").appendLn("1 message appender")
                .openTab().appendLn("2 message appender").appendLn("2 message appender")
                .closeTab().appendLn("3 message appender").appendLn("3 message appender")
                .openTab().appendLn("4 message appender");
        System.out.println(messageAppender);
    }
}
