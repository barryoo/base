package com.barry.common.core.util;

import com.barry.common.core.constants.SymbolConst;
import com.barry.common.core.enums.FileType;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author barry chen
 * @date 2023/9/8 16:06
 */
public class ZipUtil {

    /**
     * 解压文件, 压缩文件中只包含一个文件.
     *
     * @param zipFilePath
     * @return
     */
    public static File unzipSingle(String zipFilePath) {
        return Opt.ofEmptyable(unzip(zipFilePath)).map(list -> list.get(0)).orElseThrow(() -> new RuntimeException("unzip failed, unknown reason"));
    }

    /**
     * 解压到压缩文件所在目录
     * @param zipFilePath
     * @return
     */
    public static List<File> unzip(String zipFilePath) {
        return unzip(zipFilePath, new File(zipFilePath).getParent());
    }

    /**
     * 解压缩 ZIP 文件
     *
     * @param zipFilePath  要解压缩的 ZIP 文件的路径
     * @param outputFolder 解压缩后的文件输出目录
     * @return 解压出的所有文件
     * @throws IOException
     */
    public static List<File> unzip(String zipFilePath, String outputFolder) {
        File zipFile = new File(zipFilePath);
        File outputFolderFile = new File(outputFolder);
        if (!outputFolderFile.exists()) {
            outputFolderFile.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            List<File> files = new ArrayList<>();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File file = new File(outputFolderFile, entryName);
                files.add(file);
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    FileUtils.createFile(file.getAbsolutePath());
                    fileOutputStream = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.close();
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            return files;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 压倒单个文件. 使用"原文件名.zip"作为压缩后的文件名.
     * @param sourceFile
     */
    public static File zipSingle(@Nonnull File sourceFile){
        String zipFilePath = sourceFile.getAbsolutePath() + SymbolConst.DOT + FileType.Compressed.ZIP;
        zip(Lists.newArrayList(sourceFile), zipFilePath);
        return new File(zipFilePath);
    }

    /**
     * 压缩文件或文件夹
     * 压缩后保留每个文件之间相对目录关系, 压缩文件的根目录从 所有文件中的同一个上级目录开始.
     *
     * @param sourceFileList 要压缩的文件或文件夹
     * @param zipFilePath    压缩后的 ZIP 文件的路径
     * @throws IOException
     */
    public static void zip(@Nonnull List<File> sourceFileList, @Nonnull String zipFilePath) {
        Assert.checkNotEmpty(sourceFileList, "sourceFileList can not be empty");
        sourceFileList.forEach(sf -> Assert.check(Objects.nonNull(sf) && sf.exists(), "sourceFile can not be null and must be exists"));
        //找到所有文件的相同的上级目录
        String[] sourceFilePathArray = sourceFileList.stream().map(File::getAbsolutePath).collect(Collectors.toList()).toArray(new String[1]);
        int diffIndex = StringUtils.indexOfDifference(sourceFilePathArray);
        if (diffIndex == -1) {
            //只传了一个文件,或者传的所有文件都是同一个文件.
            zipFlat(sourceFileList.stream().distinct().collect(Collectors.toList()), zipFilePath);
        } else {
            Map<File, String> fileMap = sourceFileList.stream().distinct().collect(Collectors.toMap(Function.identity(), f -> StringUtils.substring(f.getAbsolutePath(), diffIndex)));
            zip(fileMap, zipFilePath);
        }
    }

    /**
     * 压缩文件或文件夹, 所有文件将处于一级目录.
     * 注意: sourceFileList仅支持同级目录下的文件或文件夹. 如果你想要把处于不同目录下的文件或文件夹压缩到一起, 那么他们在压缩文件中会处于同一级目录下.
     *
     * @param sourceFileList
     * @param zipFilePath
     * @throws IOException
     */
    public static void zipFlat(@Nonnull List<File> sourceFileList, @Nonnull String zipFilePath) {
        Map<File, String> sourceFileMap = sourceFileList.stream().collect(Collectors.toMap(Function.identity(), file -> file.getName()));
        zip(sourceFileMap, zipFilePath);
    }

    /**
     * 压缩文件或文件夹,根据传入的压缩目标路径, 压缩后的文件将处于不同的目录.
     * /home/a/b/c.txt - a/b/c.txt  压缩后的路径为: a/b/c.txt
     * /home/e/f/g.txt - a/b/g.txt  压缩后的路径为: e/f/g.txt
     *
     * @param sourceFileMap 要压缩的文件或文件夹, 以及在压缩文件中的路径. key:file value:destinationFilePath. 如果value为空, 则表示压缩文件内的一级目录.
     * @param zipFilePath   压缩后的 ZIP 文件的路径
     * @throws IOException
     */
    public static void zip(@Nonnull Map<File, String> sourceFileMap, @Nonnull String zipFilePath) {
        Objects.requireNonNull(sourceFileMap);
        Set<File> sourceFileSet = sourceFileMap.keySet();
        Assert.checkNotEmpty(sourceFileSet, "sourceFile can not be empty");
        sourceFileSet.forEach(sf -> Assert.check(Objects.nonNull(sf) && sf.exists(), "sourceFile can not be null and must be exists"));

        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);) {
            for (Map.Entry<File, String> e : sourceFileMap.entrySet()) {
                addToZip(e.getKey(), e.getValue(), zipOutputStream);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addToZip(File file, String destinationFilePath, ZipOutputStream zipOutputStream) throws IOException {
        if (file.isFile()) {
            addFileToZip(file, destinationFilePath, zipOutputStream);
        } else if (file.isDirectory()) {
            addFolderToZip(file, destinationFilePath, zipOutputStream);
        }
    }

    private static void addFolderToZip(File sourceFolderFile, String destinationFilePath, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = sourceFolderFile.listFiles();
        for (File file : files) {
            addToZip(file, destinationFilePath + "/" + file.getName(), zipOutputStream);
        }
    }

    private static void addFileToZip(File file, String destinationFilePath, ZipOutputStream zipOutputStream) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(destinationFilePath);
        zipOutputStream.putNextEntry(zipEntry);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = fileInputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, len);
        }
        fileInputStream.close();
    }

}
