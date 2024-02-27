package com.barry.common.core.enums;

/**
 * @author barry chen
 * @date 2021/3/8 16:32
 */
public enum EnvEnum {
    /**
     * 本地环境
     */
    LOCAL("local", "local"),
    /**
     * 开发环境
     */
    DEV("dev", "develop"),
    /**
     * 测试环境
     */
    TEST("test", "test"),
    /**
     * 生产环境
     */
    PROD("prod", "production"),
    ;

    private String name;
    private String fullName;

    EnvEnum() {
    }

    EnvEnum(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
