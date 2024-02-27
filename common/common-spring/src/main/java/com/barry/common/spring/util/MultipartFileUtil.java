package com.barry.common.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class MultipartFileUtil {

    private MultipartFileUtil() {
    }

    public static List<CommonsMultipartFile> getCommonsMultipartFileList(byte[] aByte, String fileName, String name) {
        List<CommonsMultipartFile> multipartFiles = new ArrayList<>();
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(name, "text/plain", true, fileName);
        OutputStream os = null;
        ByteArrayInputStream fis = null;
        int bytesRead;
        try {
            fis = new ByteArrayInputStream(aByte);
            os = item.getOutputStream();
            while ((bytesRead = fis.read(aByte, 0, aByte.length)) != -1) {
                os.write(aByte, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CommonsMultipartFile commonsMultipartFile = new CommonsMultipartFile(item);
        multipartFiles.add(commonsMultipartFile);
        return multipartFiles;
    }

    public static CommonsMultipartFile from(File file, String itemName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(itemName, "text/plain", true, file.getName());
        OutputStream os = null;
        FileInputStream fis = null;
        int bytesRead;
        try {
            fis = new FileInputStream(file);
            os = item.getOutputStream();
            IOUtils.copy(fis, os);
        } catch (IOException e) {
            log.error("convert file to multipartFile error. ", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("convert file to multipartFile, occur error when close stream.", e);
            }
        }
        CommonsMultipartFile commonsMultipartFile = new CommonsMultipartFile(item);
        return commonsMultipartFile;
    }
}
