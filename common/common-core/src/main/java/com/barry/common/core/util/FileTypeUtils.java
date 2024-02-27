package com.barry.common.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author barrychen
 */
public final class FileTypeUtils {

    public static final List<String> IMG_TYPE_LIST = Arrays.asList(new String[]{"bmp", "jpg", "jpeg", "png", "gif"});
    public static final List<String> COMPRESSED_FILE_TYPE_LIST = Arrays.asList(new String[]{"rar", "zip", "arj", "gz", "z"});
    public static final String QUESTION_MARKETS = "?";

    private FileTypeUtils() {
    }

    /**
     * 判断是否是图片
     *
     * @param fileType
     * @return
     */
    public static boolean isImage(String fileType) {
        return IMG_TYPE_LIST.contains(fileType.toLowerCase());
    }

    /**
     * 判断是压缩文件
     *
     * @param fileType
     * @return
     */
    public static boolean isCompressedFile(String fileType) {
        return COMPRESSED_FILE_TYPE_LIST.contains(fileType.toLowerCase());
    }

    public static String getFileTypeByPath(String path) {
        if (!StringUtils.isBlank(path)) {
            int index = path.lastIndexOf("/");
            if (index >= 0) {
                path = path.substring(index + 1);
            }
            index = path.lastIndexOf(".");
            if (index >= 0) {
                String fileExtension = path.substring(index + 1);
                if (fileExtension.indexOf(QUESTION_MARKETS) > -1) {
                    fileExtension = fileExtension.substring(0, fileExtension.indexOf("?"));
                }
                return fileExtension.toLowerCase();
            }
        }
        return null;
    }

    /**
     * 通过文件头换取文件类型
     *
     * @param file
     * @return
     */
    public static String getFileTypeByFile(File file) {
        if (file.exists() && file.isFile()) {
            InputStream is = null;
            try {
                byte[] b = new byte[50];
                is = new FileInputStream(file);
                is.read(b);
                return getFileTypeByStream(b);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static String getFileTypeByStream(byte[] b) {
        String fileHex = String.valueOf(getFileHexString(b));
        FileTypeHexEnum[] enums = FileTypeHexEnum.values();
        for (FileTypeHexEnum hexEnum : enums) {
            String fileTypeHex = hexEnum.getHex();
            if (fileHex.toUpperCase().startsWith(fileTypeHex)) {
                return hexEnum.getType();
            }
        }
        return null;
    }


    public static String getFileHexString(byte[] b) {
        if (b == null || b.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 文件类型
     */
    public enum FileTypeHexEnum {
        JPG("jpg", "FFD8FF", "JPEG (jpg)"),
        JPEG("jpeg", "FFD8FF", "JPEG (jpg)"),
        PNG("png", "89504E47", "PNG (png)"),
        GIF("gif", "47494638", "GIF (gif)"),
        TIF("tif", "49492A00", "TIFF (tif)"),
        BMP("bmp", "424D", "Windows Bitmap (bmp)"),
        DWG("dwg", "41433130", "CAD (dwg)"),
        HTML("html", "68746D6C3E", "HTML (html)"),
        RTF("rtf", "7B5C727466", "Rich Text Format (rtf)"),
        XML("xml", "3C3F786D6C", "xml"),
        ZIP("zip", "504B0304", "zip"),
        RAR("rar", "52617221", "rar"),
        PSD("psd", "38425053", "PhotoShop (psd)"),
        EML("eml", "44656C69766572792D646174653A", "Email"),
        DBX("dbx", "CFAD12FEC5FD746F", "Outlook Express (dbx)"),
        PST("pst", "2142444E", "Outlook (pst)"),
        XLS("xls", "D0CF11E0", "MS Word"),
        DOC("doc", "D0CF11E0", "MS Excel 注意：word 和 excel的文件头一样"),
        MDB("mdb", "5374616E64617264204A", "MS Access (mdb)"),
        WPD("wpd", "FF575043", "WordPerfect (wpd)"),
        EPS("eps", "252150532D41646F6265", "eps"),
        PS("ps", "252150532D41646F6265", "ps"),
        PDF("pdf", "255044462D312E", "Adobe Acrobat (pdf)"),
        QDF("qdf", "AC9EBD8F", "Quicken (qdf)"),
        PWL("pwl", "E3828596", "Windows Password (pwl)"),
        WAV("wav", "57415645", "Wave (wav)"),
        AVI("avi", "41564920", "avi"),
        RAM("ram", "2E7261FD", "Real Audio (ram)"),
        RM("rm", "2E524D46", "Real Media (rm)"),
        MPG("mpg", "000001BA", "mpg"),
        MOV("mov", "6D6F6F76", "QuickTime (mov)"),
        ASF("asf", "3026B2758E66CF11", "Windows Media (asf)"),
        MID("mid", "4D546864", "MIDI (mid)"),
        NULL("null", "null", "null");

        private final String type;
        private final String hex;
        private final String desc;

        FileTypeHexEnum(String type, String hex, String desc) {
            this.type = type;
            this.hex = hex;
            this.desc = desc;
        }

        public static FileTypeHexEnum typeOf(String type) {
            for (FileTypeHexEnum hexEnum : values()) {
                if (hexEnum.getType().equalsIgnoreCase(type)) {
                    return hexEnum;
                }
            }
            return NULL;
        }

        public String getType() {
            return type;
        }

        public String getHex() {
            return hex;
        }

        public String getDesc() {
            return desc;
        }
    }
}
