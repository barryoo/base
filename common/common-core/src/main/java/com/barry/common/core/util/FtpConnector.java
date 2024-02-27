package com.barry.common.core.util;

import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.BusinessException;
import com.barry.common.core.exception.SystemErrorCode;
import com.barry.common.core.bean.FtpConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * FTP操作工具类
 *
 */
@Slf4j
public class FtpConnector extends FTPClient implements AutoCloseable {
    /**
     * Default charset
     */
    private static final String LOCAL_CHARSET = "UTF-8";
    private static final int CONNECT_MAX_TRY_TIMES = 3;
    private static final int TIMEOUT = 60 * 1000;
    private static final int RETRY_WAIT_DURATION_IN_MILLISECONDS = 2000;
    private static final int BUFFER_SIZE = 1024;
    private final FTPClient ftpClient;

    private FtpConnector(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    /**
     * 根据FTP配置创建连接
     *
     * @param ftpConfig FTP配置信息
     * @return FTPClient
     */
    public static synchronized FtpConnector connect(FtpConfig ftpConfig) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(ftpConfig.getFtpEncode());
        // 默认超时时间60秒
        ftpClient.setDefaultTimeout(TIMEOUT);
        ftpClient.setConnectTimeout(TIMEOUT);
        ftpClient.setDataTimeout(TIMEOUT);

        int tryTimes = 0;
        while (tryTimes <= CONNECT_MAX_TRY_TIMES) {
            log.info("ready to create ftp connection, host:{}, port:{}, try times: {}", ftpConfig.getHost(), ftpConfig.getPort(), tryTimes);
            try {
                ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
                ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
                int reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    log.warn("ftp connection failed, reply: {}", reply);
                    ftpClient.disconnect();
                    throw new ApplicationException(SystemErrorCode.SYS_FTP_CONNECT_ERROR, "server reply:" + reply);
                }
                log.info("FTP Session connected.");
                return new FtpConnector(ftpClient);
            } catch (IOException e) {
                try {
                    log.info("Please retry after 2 seconds, retry times: " + tryTimes);
                    Thread.sleep(RETRY_WAIT_DURATION_IN_MILLISECONDS);
                } catch (InterruptedException ie) {
                    throw new ApplicationException(SystemErrorCode.SYS_FTP_CONNECT_ERROR, ie, "retry wait error, try times:" + tryTimes);
                }
                if (tryTimes > CONNECT_MAX_TRY_TIMES) {
                    throw new ApplicationException(SystemErrorCode.SYS_FTP_CONNECT_ERROR, e, "connect error, try times:" + tryTimes);
                }
            }
            tryTimes++;
        }
        throw new ApplicationException(SystemErrorCode.SYS_FTP_CONNECT_ERROR, "connect error, try times:" + tryTimes);
    }

    /**
     * 判断路径下是否存在文件
     *
     * @param path 文件地址
     * @return 结果
     */
    public boolean existFile(String path) {
        boolean flag = false;
        FTPFile[] ftpFileArr;
        try {
            ftpFileArr = ftpClient.listFiles(path);
        } catch (IOException e) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_LIST_FILE_ERROR, "list file error, path:" + path);
        }
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 下载文件
     *
     * @param ftpPath   FTP文件路径
     * @param localPath 下载本地路径
     * @param fileName  FTP文件名
     */
    public File downloadFile(String ftpPath, String localPath, String fileName) {
        return downloadFile(ftpPath, localPath, fileName, 0L, 0, 0L);
    }

    /**
     * 下载文件时对文件进行检测后再下载
     *
     * @param ftpPath         下载目录
     * @param localPath       本地目标路径
     * @param fileName        文件名
     * @param scanWaitTime    每隔多少秒扫描一次远程文件的大小，单位毫秒
     * @param sizeSameCount   文件大小需要相同多少次才允许开始下载
     * @param scanWaitTimeout 扫描等待超时时间
     * @return download file
     */
    public File downloadFile(String ftpPath, String localPath, String fileName, Long scanWaitTime, Integer sizeSameCount, Long scanWaitTimeout) {
        // 当前连续相同次数
        int currentSizeSameCount = 0;
        // 是否超时
        boolean timeoutFlag = false;
        long tempFileSize = 0L;
        Long timeout = System.currentTimeMillis() + scanWaitTimeout;
        log.info(String.valueOf(timeout));
        FTPFile[] files;
        OutputStream outputStream;
        File file;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            file = new File(localPath + File.separatorChar + fileName);
            outputStream = Files.newOutputStream(file.toPath());
            ftpClient.changeWorkingDirectory(ftpPath);
            files = ftpClient.listFiles(ftpPath);
            do {
                log.info("Current Size Same Count : " + currentSizeSameCount);
                if (currentSizeSameCount >= sizeSameCount) {
                    break;
                } else {
                    files = ftpClient.listFiles(ftpPath);
                    for (FTPFile f : files) {
                        if (f.getName().equalsIgnoreCase(fileName)) {
                            if (tempFileSize == f.getSize()) {
                                currentSizeSameCount++;
                            }
                            tempFileSize = f.getSize();
                            Thread.sleep(scanWaitTime);
                        }
                    }
                    if (System.currentTimeMillis() > timeout) {
                        timeoutFlag = true;
                    }
                }
            } while (!timeoutFlag);
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_DOWNLOAD_FILE_ERROR, e, "list download file with error, path:" + ftpPath + ", fileName:" + fileName);
        }
        // 超时则返回空并记录异常
        if (timeoutFlag) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_DOWNLOAD_FILE_ERROR, "download file timeout, path:" + ftpPath + ", fileName:" + fileName);
        }
        try {
            log.info("Start download.");
            for (FTPFile f : files) {
                if (f.getName().equalsIgnoreCase(fileName)) {
                    for (int retryTimes = 0; retryTimes < CONNECT_MAX_TRY_TIMES; retryTimes++) {
                        try {
                            ftpClient.retrieveFile(fileName, outputStream);
                            outputStream.close();
                            return file;
                        } catch (Exception e) {
                            try {
                                log.info("Please retry after 2 seconds, retry times: " + retryTimes);
                                Thread.sleep(RETRY_WAIT_DURATION_IN_MILLISECONDS);
                            } catch (InterruptedException ie) {
                                throw new ApplicationException(SystemErrorCode.SYS_FTP_DOWNLOAD_FILE_ERROR, "download file interrupted, path:" + ftpPath + ", fileName:" + fileName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_DOWNLOAD_FILE_ERROR, e, "Ftp file download exception, path:" + ftpPath + ", fileName:" + fileName);
        } finally {
            disconnect();
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param ftpPath     FTP路径
     * @return 上传结果
     */
    public synchronized boolean uploadFile(InputStream inputStream, String fileName, String ftpPath) {
        try {
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.setBufferSize(BUFFER_SIZE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            return true;
        } catch (IOException e) {
            throw new BusinessException(SystemErrorCode.SYS_FTP_UPLOAD_FILE_ERROR, e, "Ftp upload file exception");
        } finally {
            disconnect();
        }
    }

    /**
     * 检查路径是否包含子目录，没有则创建
     *
     * @param ftpPath       FTP路径
     * @param subfolderName 子目录名
     * @return 创建结果
     */
    public boolean checkSubfolder(String ftpPath, String subfolderName) {
        try {
            ftpClient.changeWorkingDirectory(ftpPath);
            InputStream is = ftpClient.retrieveFileStream(new String(subfolderName.getBytes(LOCAL_CHARSET)));
            if (is == null || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                return createSubfolder(ftpPath, subfolderName);
            }
            is.close();
            return true;
        } catch (IOException e) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_CREATE_FOLDER_ERROR, e, "create sub folder error, path:" + ftpPath + ", subfolderName:" + subfolderName);
        }
    }

    /**
     * 在路径创建子目录
     *
     * @param ftpPath       FTP路径
     * @param subfolderName 子目录名
     * @return 创建结果
     */
    public synchronized boolean createSubfolder(String ftpPath, String subfolderName) {
        try {
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.makeDirectory(subfolderName);
            return true;
        } catch (IOException e) {
            throw new ApplicationException(SystemErrorCode.SYS_FTP_CREATE_FOLDER_ERROR, e, "create sub folder error, path:" + ftpPath + ", subfolderName:" + subfolderName);
        }
    }

    /**
     * 断开与远程服务器的连接
     */
    public void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new ApplicationException(SystemErrorCode.SYS_FTP_DISCONNECT_ERROR, e, "Ftp disconnect exception");
            }
            log.info("FTP Session closed.");
        }
    }

    @Override
    public void close() {
        disconnect();
    }
}
