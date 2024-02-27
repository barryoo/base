package com.barry.common.doc.enums;

import com.barry.common.core.util.FileTypeUtils;
import com.barry.common.core.util.StringUtils;

/**
 * @author Zhong.Chengzhe
 * @name DocumentContentType
 * @description
 * @date 2023/1/9
 */
public enum DocumentContentType {
    /**
     * png
     */
    PNG,
    /**
     * jpg
     */
    JPG,
    /**
     * jpeg
     */
    JPEG,
    /**
     * pdf
     */
    PDF,
    /**
     * docx
     */
    DOCX,
    /**
     * text
     */
    TEXT,
    /**
     * zpl
     */
    ZPL,
    /**
     * byte
     */
    BYTE;

    public static DocumentContentType getContentType(String file) {
        String fileType = FileTypeUtils.getFileTypeByPath(file);
        return DocumentContentType.valueOf(StringUtils.upperCase(fileType));
    }
}
