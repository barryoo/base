package com.barry.common.core.util;

import com.barry.common.core.constants.MailConst;
import com.barry.common.core.enums.MailEnum;
import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.SystemErrorCode;
import com.barry.common.core.bean.MailConfig;
import com.barry.common.core.bean.MailMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * 邮件基础服务工具类
 *
 */
@Slf4j
public final class MailUtils {
    private static final int RETRY_WAIT = 2000;
    private static final HashMap<String, Store> MAIL_STORES = new HashMap<>();
    private static final Integer RETRY_TIMES = 3;

    private MailUtils() {
    }

    /**
     * 创建收邮件的Store对象，重连3次
     *
     * @param mailConfig 邮箱配置
     * @return 连接的Store对象
     */
    public static Store getStore(MailConfig mailConfig) {
        try {
            //获取默认邮箱配置,创建Session实例对象
            Session session = MailEnum.valueOf(mailConfig.getType()).getSession();
            Store store;
            if (MAIL_STORES.containsKey(mailConfig.getUsername())) {
                store = MAIL_STORES.get(mailConfig.getUsername());
            } else {
                store = session.getStore(MailEnum.valueOf(mailConfig.getType()).getProtocol());
            }
            if (!store.isConnected()) {
                for (int retryTimes = 0; retryTimes < RETRY_TIMES; retryTimes++) {
                    try {
                        store.connect(MailEnum.valueOf(mailConfig.getType()).getServer(), mailConfig.getUsername(), mailConfig.getPassword());
                        MAIL_STORES.put(mailConfig.getUsername(), store);
                        return store;
                    } catch (MessagingException ex) {
                        try {
                            log.info("Please retry after 2 seconds, retry times: " + retryTimes);
                            Thread.sleep(RETRY_WAIT);
                        } catch (InterruptedException e) {
                            throw new ApplicationException(SystemErrorCode.SYS_MAIL_ERROR, e, "mail get store exception");
                        }
                    }
                }
            }
            MAIL_STORES.put(mailConfig.getUsername(), store);
            return store;
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_ERROR, e, "mail get store exception");
        }
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @param part 邮件体
     * @return 邮件中存在附件返回true，不存在返回false
     */
    public static boolean isContainAttachment(Part part) {
        boolean attachFlag = false;
        try {
            if (part.isMimeType(MailConst.MULTIPART)) {
                MimeMultipart multipart = (MimeMultipart) part.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String disposition = bodyPart.getDisposition();
                    if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                        attachFlag = true;
                    } else if (bodyPart.isMimeType(MailConst.MULTIPART)) {
                        attachFlag = isContainAttachment((Part) bodyPart);
                    } else {
                        String contentType = bodyPart.getContentType();
                        if (contentType.contains(MailConst.APPLICATION)) {
                            attachFlag = true;
                        }

                        if (contentType.contains(MailConst.NAME)) {
                            attachFlag = true;
                        }
                    }
                }
            } else if (part.isMimeType(MailConst.RFC822)) {
                attachFlag = isContainAttachment((Part) part.getContent());
            }
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_CONTENT_ERROR, e, "get mail contain attachment exception");
        }
        return attachFlag;
    }

    /**
     * 保存附件
     *
     * @param part    邮件中多个组合体中的其中一个组合体
     * @param destDir 附件保存目录
     * @param message message
     */
    public static void saveAttachment(Part part, String destDir, MailMessage message) {
        try {
            if (part.isMimeType(MailConst.MULTIPART)) {
                Multipart multipart = (Multipart) part.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    String disp = bodyPart.getDisposition();
                    if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                        InputStream is = bodyPart.getInputStream();
                        message.getAttachments().add(saveFile(is, destDir, MimeUtility.decodeText(bodyPart.getFileName())));
                    } else if (bodyPart.isMimeType(MailConst.MULTIPART)) {
                        saveAttachment(bodyPart, destDir, message);
                    } else {
                        String contentType = bodyPart.getContentType();
                        if (contentType.contains(MailConst.NAME) || contentType.contains(MailConst.APPLICATION)) {
                            message.getAttachments().add(saveFile(bodyPart.getInputStream(), destDir, MimeUtility.decodeText(bodyPart.getFileName())));
                        }
                    }
                }
            } else if (part.isMimeType(MailConst.RFC822)) {
                saveAttachment((Part) part.getContent(), destDir, message);
            }
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_ATTACHMENT_ERROR, e, "save mail attachment exception");
        }
    }

    /**
     * 读取输入流中的数据保存至缓存目录
     *
     * @param is       输入流
     * @param fileName 文件名
     * @param destDir  文件存储目录
     * @return 附件
     */
    private static File saveFile(InputStream is, String destDir, String fileName) {
        File file = new File(destDir + File.separatorChar + fileName);
        try (
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            IOUtils.copy(bis, bos);
            return file;
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_ATTACHMENT_ERROR, e, "mail save file exception");
        }
    }

    /**
     * 获取邮件正文内容
     *
     * @param multipart multipart
     * @param message   message
     */
    public static void getBodyText(Multipart multipart, MailMessage message) {
        try {
            int count = multipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    message.getBodyText().append(bodyPart.getContent());
                } else if (bodyPart.isMimeType("text/html")) {
                    message.getBodyText().append(bodyPart.getContent());
                } else if (bodyPart.isMimeType("multipart/*")) {
                    Multipart multi = (Multipart) bodyPart.getContent();
                    getBodyText(multi, message);
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_CONTENT_ERROR, e, "get mail body text exception");
        }
    }

    /**
     * 判断此邮件是否已读，如果未读返回返回false,反之返回true
     *
     * @param mimeMessage mimeMessage
     * @return isNew
     */
    public boolean isNew(MimeMessage mimeMessage) {
        try {
            boolean isNew = false;
            Flags flags = ((Message) mimeMessage).getFlags();
            Flags.Flag[] flag = flags.getSystemFlags();
            for (Flags.Flag f : flag) {
                if (f == Flags.Flag.SEEN) {
                    isNew = true;
                    break;
                }
            }
            return isNew;
        } catch (MessagingException e) {
            throw new ApplicationException(SystemErrorCode.SYS_MAIL_CONTENT_ERROR, e, "get mail status exception");
        }
    }
}
