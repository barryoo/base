package com.barry.common.core.util;

import com.barry.common.core.bean.FtpConfig;
import com.barry.common.core.bean.SftpConfig;
import com.jcraft.jsch.SftpException;
import lombok.extern.java.Log;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

import java.io.IOException;
import java.util.Vector;

/**
 * @name FtpConnectorTest
 * @description
 * @date 2022/11/21
 */
@Log
public class FtpConnectorTest {

    @Test
    public void testFtp() throws IOException {
        FtpConfig ftpConfig = new FtpConfig();
        ftpConfig.setHost("192.168.2.88");
        ftpConfig.setPort(21);
        ftpConfig.setUsername("shuqian");
        ftpConfig.setPassword("Coozo0628");
        ftpConfig.setFtpEncode("UTF-8");
        ftpConfig.setFtpFilePath("/test");
        FtpConnector f = FtpConnector.connect(ftpConfig);
        FTPFile[] ftpFiles = f.getFtpClient().listFiles();
        for (FTPFile file : ftpFiles) {
            log.info(file.getName());
        }
        f.disconnect();
    }

    @Test
    public void testSftp() throws SftpException {
        SftpConfig sftpConfig = new SftpConfig();
        sftpConfig.setHost("192.168.2.88");
        sftpConfig.setPort(22);
        sftpConfig.setUsername("server");
        sftpConfig.setPassword("Coozo0628");
        sftpConfig.setSftpEncode("UTF-8");
        sftpConfig.setSftpFilePath("/test");
        SftpConnector f = SftpConnector.connect(sftpConfig);
        Vector v = f.getChannelSftp().ls("/");
        log.info(v.toString());
        f.disconnect();
    }

}
