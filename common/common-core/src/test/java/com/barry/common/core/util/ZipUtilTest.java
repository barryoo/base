package com.barry.common.core.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;

/**
 * @author barry chen
 * @date 2023/9/8 16:26
 */
@Slf4j
public class ZipUtilTest {
    @Test
    public void zipTest() {
        File file = new File("../common-core/pom.xml");
        File folder = new File("../common-core/src/main/java/com/barry/common/core/constants");

        String zipFilePath = "../common-core/target/test.flat.zip";
        ZipUtil.zipFlat(Lists.newArrayList(file, folder), zipFilePath);
        File zipFile = new File(zipFilePath);
        log.info("拉平压缩文件结束, isExists={}", zipFile.exists());

        String zipFilePath2 = "../common-core/target/test.zip";
        ZipUtil.zip(Lists.newArrayList(file, folder), zipFilePath2);
        File zipFile2 = new File(zipFilePath2);
        log.info("拉平压缩文件结束, isExists={}", zipFile2.exists());

        String zipFilePath3 = "../common-core/target/test.single.zip";
        ZipUtil.zip(Lists.newArrayList(file), zipFilePath3);
        File zipFile3 = new File(zipFilePath3);
        log.info("拉平压缩文件结束, isExists={}", zipFile3.exists());

    }

    @Test
    public void unZipTest() {
        zipTest();
        ZipUtil.unzip(new File("../common-core/target/test.zip").getAbsolutePath());
        ZipUtil.unzipSingle(new File("../common-core/target/test.single.zip").getAbsolutePath());
    }
}
