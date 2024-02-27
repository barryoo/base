package com.barry.common.core.util;

import com.barry.common.core.constants.CommonConst;
import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.SystemErrorCode;
import com.barry.common.core.bean.SftpConfig;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * SFTP 操作工具类
 *
 */
@Slf4j
public class SftpConnector implements AutoCloseable {
    private final static Integer RETRY_TIMES = 3;
    private final static int TIMEOUT = 60 * 1000;
    private final static int RETRY_WAIT = 2000;
    private final ChannelSftp channelSftp;

    /**
     * 私有构造器, 只允许使用SftpConnector.connect来构造
     */
    private SftpConnector(ChannelSftp sftp) {
        this.channelSftp = sftp;
    }

    /**
     * 返回SFTP服务器
     *
     * @return SFTP连接
     */
    public ChannelSftp getChannelSftp() {
        return channelSftp;
    }

    /**
     * 连接sftp服务器
     *
     * @param sftpConfig FTP配置信息
     * @return 如果连接失败, 返回null
     */
    public static SftpConnector connect(SftpConfig sftpConfig) {
        for (int retryTimes = 0; retryTimes < RETRY_TIMES; retryTimes++) {
            try {
                JSch jsch = new JSch();
                if (StringUtils.isNotBlank(sftpConfig.getKeyPath())) {
                    jsch.addIdentity(sftpConfig.getKeyPath());
                }
                Session sshSession = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
                if (StringUtils.isNotBlank(sftpConfig.getPassword())) {
                    sshSession.setPassword(sftpConfig.getPassword());
                }
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", CommonConst.NO_STR);
                sshSession.setConfig(sshConfig);
                sshSession.setTimeout(TIMEOUT);
                sshSession.connect();
                log.info("SFTP Session connected.");
                Channel channel = sshSession.openChannel("sftp");
                channel.connect();
                ChannelSftp sftp = (ChannelSftp) channel;
                return new SftpConnector(sftp);
            } catch (Exception e) {
                try {
                    log.info("Please retry after 2 seconds, retry times: " + retryTimes);
                    Thread.sleep(RETRY_WAIT);
                } catch (InterruptedException ie) {
                    throw new ApplicationException(SystemErrorCode.SYS_SFTP_CONNECT_ERROR, "sftp connect error");
                }
            }
        }
        throw new ApplicationException(SystemErrorCode.SYS_SFTP_CONNECT_ERROR, "sftp connect timeout");
    }

    /**
     * 上传文件
     *
     * @param sftpPath   上传目录, 后缀不能有/
     * @param uploadFile 文件名
     * @return 上传结果
     */
    public synchronized boolean uploadFile(String sftpPath, String uploadFile) {
        File file = new File(uploadFile);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            channelSftp.put(fileInputStream, sftpPath + File.separatorChar + file.getName());
            fileInputStream.close();
            return true;
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_UPLOAD_FILE_ERROR, e, "sftp upload file error");
        }
    }

    /**
     * 文件重命名, 可以用来移动文件
     *
     * @param oldPath 旧路径
     * @param newPath 新路径
     * @return 是否成功
     */
    public boolean rename(String oldPath, String newPath) {
        try {
            channelSftp.rename(oldPath, newPath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }
    /**
     * 删除文件
     *
     * @param sftpPath   要删除文件所在目录, 后缀不能有/
     * @param deleteFile 要删除的文件
     */
    public void deleteFile(String sftpPath, String deleteFile) {
        try {
            channelSftp.rm(sftpPath + File.separatorChar + deleteFile);
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_LIST_FILE_ERROR, e, "Sftp get last modified time exception");
        }
    }


    /**
     * 下载文件
     *
     * @param sftpPath  下载目录, 后缀不能有/
     * @param localPath 存在本地的路径
     * @param fileName  文件名
     * @return 下载文件
     */
    public File downloadFile(String sftpPath, String localPath, String fileName) {
        return downloadFile(sftpPath, localPath, fileName, 0L, 0, 0L);
    }

    /**
     * 下载文件时对文件进行检测后再下载
     *
     * @param sftpPath        下载目录
     * @param localPath       本地目标路径
     * @param fileName        文件名
     * @param scanWaitTime    每隔多少秒扫描一次远程文件的大小，单位毫秒
     * @param sizeSameCount   文件大小需要相同多少次才允许开始下载
     * @param scanWaitTimeout 扫描等待超时时间
     * @return download file
     */
    public File downloadFile(String sftpPath, String localPath, String fileName, Long scanWaitTime, Integer sizeSameCount, Long scanWaitTimeout) {
        // 当前连续相同次数
        int currentSizeSameCount = 0;
        // 是否超时
        boolean timeoutFlag = false;
        long tempFileSize = 0L;
        long timeout = System.currentTimeMillis() + scanWaitTimeout;
        try {
            do {
                log.info("Current Size Same Count : " + currentSizeSameCount);
                if (currentSizeSameCount >= sizeSameCount) {
                    break;
                } else {
                    SftpATTRS attr = channelSftp.stat(sftpPath + File.separatorChar + fileName);
                    if (tempFileSize == attr.getSize()) {
                        currentSizeSameCount++;
                    }
                    tempFileSize = attr.getSize();
                    Thread.sleep(scanWaitTime);
                    if (System.currentTimeMillis() > timeout) {
                        timeoutFlag = true;
                    }
                }
            } while (!timeoutFlag);
            // 超时则返回空并记录异常
            if (timeoutFlag) {
                throw new ApplicationException(SystemErrorCode.SYS_SFTP_DOWNLOAD_FILE_ERROR, "Sftp file download file timeout");
            }
            log.info("Start download.");
            File file = FileUtils.getFile(localPath, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            channelSftp.get(sftpPath + File.separatorChar + fileName, fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_DOWNLOAD_FILE_ERROR, e, "Sftp file download exception");
        }
    }

    /**
     * 获取指定路径下的文件列表
     *
     * @param path 文件夹绝对路径
     * @return 文件和文件夹名列表
     */
    public List<String> listFileName(String path) {
        try {
            Vector fileVector = channelSftp.ls(path);
            if (CollectionUtils.isEmpty(fileVector)) {
                return Collections.emptyList();
            }
            List<String> fileNameList = new ArrayList<>(fileVector.size());
            for (Object o : fileVector) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) o;
                fileNameList.add(lsEntry.getFilename());
            }
            return fileNameList;
        } catch (SftpException e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_LIST_FILE_ERROR, e, "Sftp list file name exception");
        }
    }

    /**
     * 获取sftp路径下文件的最后更新时间
     *
     * @param sftpPath sftpPath
     * @param fileName fileName
     * @return
     */
    public Date getLastModifiedTime(String sftpPath, String fileName) {
        try {
            SftpATTRS sftpAttrs = channelSftp.stat(sftpPath + File.separatorChar + fileName);
            // 单位是秒数，需要 * 1000才能转化为Date
            int mTime = sftpAttrs.getMTime();
            return new Date(mTime * 1000L);
        } catch (SftpException e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_LIST_FILE_ERROR, e, "Sftp get last modified time exception");
        }
    }

    /**
     * 断开与远程服务器的连接
     */
    public void disconnect() {
        try {
            channelSftp.getSession().disconnect();
            channelSftp.quit();
            channelSftp.disconnect();
            log.info("SFTP Session closed.");
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_SFTP_DISCONNECT_ERROR, e, "Sftp disconnect exception");
        }
    }

    @Override
    public void close() throws Exception {
        disconnect();
    }
}
