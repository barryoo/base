package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author barryChen
 */
@Data
public class MailMessage implements Serializable {
    private StringBuffer bodyText;
    private List<File> attachments;
    private String subject;

    public MailMessage() {
        bodyText = new StringBuffer();
        attachments = new ArrayList<>();
    }
}
