package com.barry.common.core.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页类. 用于分页接口响应
 * 结构从IPage复制而来.
 *
 * @param <T>
 */
@Data
public class CommonPage<T> implements Serializable {
    private static final long serialVersionUID = 8545996863226528798L;

    protected List<T> records = Collections.emptyList();
    protected long total = 0;
    private long current = 1;
    private long size = 10;
    private List<SqlOrderItem> orders = new ArrayList<>();;

    public CommonPage(long current, long size) {
        this.current = current;
        this.size = size;
    }

    public CommonPage(long current, long size, long total) {
        this.current = current;
        this.size = size;
        this.total = total;
    }

    public CommonPage(long current, long size, long total, List<T> records) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
    }

    public CommonPage(long current, long size, Boolean fakeTotal, List<T> records) {
        this.records = records;
        this.size = size;
        this.current = current;
        this.total = CommonPage.fakeTotal(current, size, records == null ? 0 : records.size());
    }

    /**
     * 计算虚假total.
     * 该total不是真实的数量, 只能给出是否还有下一页.
     *
     * @param current
     * @param size
     * @param currentSize
     * @return
     */
    public static long fakeTotal(long current, long size, long currentSize) {
        long total = 0;
        if (currentSize >= size) {
            total += current * size + 1;
        } else {
            total += (current - 1) * size + currentSize;
        }
        return total;
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
