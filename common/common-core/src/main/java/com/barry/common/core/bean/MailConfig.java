package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱配置
 * @author barryChen
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailConfig {
    /**
     * 邮箱用户名
     */
    private String username;
    /**
     * 邮箱密码
     */
    private String password;
    /**
     * 邮箱类型
     */
    private String type;
    /**
     * 邮件内容匹配规则
     */
    private String matchPhrase;
}
