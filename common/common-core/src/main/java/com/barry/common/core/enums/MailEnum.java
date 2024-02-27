package com.barry.common.core.enums;

import com.barry.common.core.constants.CommonConst;
import com.barry.common.core.constants.MailConst;

import javax.mail.Session;
import java.util.Properties;

/**
 * 服务器种类：腾讯企业默认配置，可以扩展
 */
public enum MailEnum {
    /**
     * 腾讯企业邮箱配置
     */
    TENCENT_EX {
        @Override
        public Session getSession() {
            Properties props = new Properties();
            props.setProperty("mail.imap.socketFactory.class", MailConst.SSL_FACTORY);
            props.setProperty("mail.imap.socketFactory.fallback", CommonConst.FALSE_STRING);
            props.setProperty("mail.transport.protocol", MailConst.EXMAIL_PROTOCOL);
            props.setProperty("mail.imap.port", MailConst.EXMAIL_PORT);
            props.setProperty("mail.imap.socketFactory.port", MailConst.EXMAIL_PORT);
            props.setProperty("mail.imap.partialfetch",CommonConst.FALSE_STRING);
            props.setProperty("mail.imap.fetchsize", MailConst.DEFAULT_FATCHSIZE);
            return Session.getDefaultInstance(props);
        }

        @Override
        public String getProtocol() {
            return MailConst.EXMAIL_PROTOCOL;
        }

        @Override
        public String getServer() {
            return MailConst.EXMAIL_IMAP_SERVER;
        }
    },
    /**
     * One Mail
     */
    ONE {
        @Override
        public Session getSession() {
            Properties props = new Properties();
            props.setProperty("mail.imap.socketFactory.class", MailConst.SSL_FACTORY);
            props.setProperty("mail.imap.socketFactory.fallback", CommonConst.FALSE_STRING);
            props.setProperty("mail.transport.protocol", MailConst.ONE_PROTOCOL);
            props.setProperty("mail.imap.port", MailConst.ONE_PORT);
            props.setProperty("mail.imap.socketFactory.port", MailConst.ONE_PORT);
            props.setProperty("mail.imap.partialfetch",CommonConst.FALSE_STRING);
            props.setProperty("mail.imap.fetchsize", MailConst.DEFAULT_FATCHSIZE);
            return Session.getDefaultInstance(props);
        }

        @Override
        public String getProtocol() {
            return MailConst.ONE_PROTOCOL;
        }

        @Override
        public String getServer() {
            return MailConst.ONE_IMAP_SERVER;
        }
    };

    /**
     * @return Default Session
     */
    public abstract Session getSession();

    /**
     * @return Default Protocol
     */
    public abstract String getProtocol();

    /**
     * @return Default Mail Server
     */
    public abstract String getServer();

}
