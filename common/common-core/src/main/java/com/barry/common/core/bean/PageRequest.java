package com.barry.common.core.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页请求
 * @author barryChen
 * @date 2020-10-13
 */
@Data
public class PageRequest {

    private long current;
    private long size;
    private List<SqlOrderItem> orders = new ArrayList<>();

    public PageRequest() {
    }

    public PageRequest(long current, long size) {
        this.current = current;
        this.size = size;
    }

    public PageRequest(long current, long size, List<SqlOrderItem> orders) {
        this.current = current;
        this.size = size;
        this.orders = orders;
    }

    public void addOrders(List<SqlOrderItem> orderItemList) {
        if (orderItemList != null && !orderItemList.isEmpty()) {
            this.orders.addAll(orderItemList);
        }
    }

    public void addOrder(SqlOrderItem orderItem) {
        if (orderItem != null) {
            this.orders.add(orderItem);
        }
    }

}
