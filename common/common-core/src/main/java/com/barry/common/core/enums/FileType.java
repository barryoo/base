package com.barry.common.core.enums;

/**
 * @author barry chen
 * @date 2023/9/11 17:03
 */
public class FileType {

    public enum Image {
        BMP("bmp"),
        JPG("jpg"),
        JPEG("jpeg"),
        PNG("png"),
        GIF("gif");

        private final String value;

        Image(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum document {
        DOC("doc"),
        DOCX("docx"),
        XLS("xls"),
        XLSX("xlsx"),
        PPT("ppt"),
        PPTX("pptx"),
        PDF("pdf"),
        TXT("txt"),
        ZPL("zpl");

        private final String value;

        document(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Compressed {
        RAR("rar"),
        ZIP("zip"),
        ARJ("arj"),
        GZ("gz"),
        SEVEN_Z("7z");

        private final String value;

        Compressed(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
