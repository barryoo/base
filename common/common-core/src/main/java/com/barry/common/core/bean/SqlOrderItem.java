package com.barry.common.core.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author barryChen
 * @date 2023/11/2 15:32
 */
@Data
public class SqlOrderItem implements Serializable {
    /**
     * 需要进行排序的字段
     */
    private String column;
    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;

    public SqlOrderItem() {
    }

    public SqlOrderItem(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }
}
