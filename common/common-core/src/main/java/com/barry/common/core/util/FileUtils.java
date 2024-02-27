package com.barry.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

/**
 * @author barrychen
 */
@Slf4j
public final class FileUtils extends org.apache.commons.io.FileUtils {

    private static final String CONTENT_DISPOSITION_FILE_NAME = "filename=";

    private FileUtils() {
    }

    /**
     * 创建单个文件
     *
     * @param descFileName 文件名，包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            log.info("文件 " + descFileName + " 已存在!");
            return false;
        }
        if (descFileName.endsWith(File.separator)) {
            log.info(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            return true;
        }

        // 创建文件
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                log.info(descFileName + " 文件创建失败!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(descFileName + " 文件创建失败!");
            return false;
        }
    }

    /**
     * 创建目录
     *
     * @param descDirName 目录名,包含路径
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createDirectory(String descDirName) {
        String descDirNames = descDirName;
        if (!descDirNames.endsWith(File.separator)) {
            descDirNames = descDirNames + File.separator;
        }
        File descDir = new File(descDirNames);
        if (descDir.exists()) {
            log.info("目录 " + descDirNames + " 已存在!");
            return false;
        }
        // 创建目录
        if (descDir.mkdirs()) {
            return true;
        } else {
            log.info("目录 " + descDirNames + " 创建失败!");
            return false;
        }

    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     */
    public static void writeToFile(String fileName, String content, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, "utf-8", append);
            log.info("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            log.error("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 要写入的文件
     */
    public static void writeToFile(String fileName, String content, String encoding, boolean append) {
        try {
            FileUtils.write(new File(fileName), content, encoding, append);
            log.info("文件 " + fileName + " 写入成功!");
        } catch (IOException e) {
            log.error("文件 " + fileName + " 写入失败! " + e.getMessage());
        }
    }

    /**
     * 获取待压缩文件在ZIP文件中entry的名字，即相对于跟目录的相对路径名
     *
     * @param dirPath 目录名
     * @param file    entry文件名
     * @return
     */
    private static String getEntryName(String dirPath, File file) {
        String dirPaths = dirPath;
        if (!dirPaths.endsWith(File.separator)) {
            dirPaths = dirPaths + File.separator;
        }
        String filePath = file.getAbsolutePath();
        // 对于目录，必须在entry名字后面加上"/"，表示它将以目录项存储
        if (file.isDirectory()) {
            filePath += "/";
        }
        int index = filePath.indexOf(dirPaths);

        return filePath.substring(index + dirPaths.length());
    }

    /**
     * 删除文件，可以删除单个文件或文件夹
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否是返回false
     */
    public static boolean delFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.info(fileName + " 文件不存在!");
            return true;
        } else {
            if (file.isFile()) {
                return FileUtils.deleteFile(fileName);
            } else {
                return FileUtils.deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 被删除的文件名
     * @return 如果删除成功，则返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.info("删除文件 " + file.getName() + " 成功!");
                return true;
            } else {
                log.info("删除文件 " + file.getName() + " 失败!");
                return false;
            }
        } else {
            log.info(file.getName() + " 文件不存在!");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirName 被删除的目录所在的文件路径
     * @return 如果目录删除成功，则返回true，否则返回false
     */
    public static boolean deleteDirectory(String dirName) {
        String dirNames = dirName;
        if (!dirNames.endsWith(File.separator)) {
            dirNames = dirNames + File.separator;
        }
        File dirFile = new File(dirNames);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.info(dirNames + " 目录不存在!");
            return true;
        }
        boolean flag = true;
        // 列出全部文件及子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtils.deleteFile(files[i].getAbsolutePath());
                // 如果删除文件失败，则退出循环
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtils.deleteDirectory(files[i]
                        .getAbsolutePath());
                // 如果删除子目录失败，则退出循环
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            log.info("删除目录失败!");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            log.info("删除目录 " + dirName + " 成功!");
            return true;
        } else {
            log.info("删除目录 " + dirName + " 失败!");
            return false;
        }

    }

    /**
     * 下载远程文件
     *
     * @param remotePath   远程地址
     * @param localDirPath 下载文件所在的目录
     * @return
     */
    public static File downloadRemoteFile(String remotePath, String localDirPath) {
        return downloadRemoteFile(remotePath, localDirPath, null);
    }

    /**
     * 下载远程文件
     *
     * @param remotePath   远程地址
     * @param localDirPath 下载后文件所在的目录
     * @param fileName     下载后的文件名
     * @return
     */
    public static File downloadRemoteFile(String remotePath, String localDirPath, String fileName) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        if (localDirPath == null) {
            localDirPath = "download";
        }
        try {
            HttpURLConnection httpUrl = (HttpURLConnection) new URL(remotePath).openConnection();
            httpUrl.connect();

            bis = new BufferedInputStream(httpUrl.getInputStream());
            byte[] b = null;
            int len = 50;

            String remoteFileName = "";
            String contentDisposition = httpUrl.getHeaderField("Content-Disposition");
            if (StringUtils.isNotBlank(fileName)) {
                remoteFileName = String.format("%s%s%s", localDirPath, File.separator, fileName);
            } else if (StringUtils.isNotBlank(contentDisposition) && contentDisposition.contains(CONTENT_DISPOSITION_FILE_NAME)) {
                remoteFileName = localDirPath + File.separator + contentDisposition.substring(contentDisposition.indexOf(CONTENT_DISPOSITION_FILE_NAME));
            } else {
                String fileType = FileTypeUtils.getFileTypeByPath(remotePath);
                if (StringUtils.isBlank(fileType)) {
                    b = new byte[len];
                    len = bis.read(b);
                    fileType = FileTypeUtils.getFileTypeByStream(b);
                }

                if (!StringUtils.isBlank(fileType)) {
                    remoteFileName = String.format("%s%s%s.%s", localDirPath, File.separator, UUIDUtils.shortUUID62(), fileType);
                }
            }

            File file = new File(remoteFileName);
            File fileDir = new File(file.getParent());
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            bos = new BufferedOutputStream(new FileOutputStream(file));
            //1、通过文件获取扩展名没读完，2、通过url获取扩展名没读
            if (len != -1) {
                if (b != null) {
                    //没读b为null
                    bos.write(b, 0, len);
                }
                len = 2048;
                b = new byte[len];
                while ((len = bis.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
            }

            bos.flush();
            httpUrl.disconnect();
            return file;
        } catch (Exception e) {
            log.error("download file error. {}", ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if(bis!=null){
                    bis.close();
                }
                if(bos!=null){
                    bos.close();
                }
            } catch (IOException e) {
                log.error("download file error. {}", ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public static String getMd5(byte[] uploadBytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getMd5(File file) {
        byte[] fileByteArray = new byte[0];
        try {
            fileByteArray = readFileToByteArray(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return getMd5(fileByteArray);
    }
}
