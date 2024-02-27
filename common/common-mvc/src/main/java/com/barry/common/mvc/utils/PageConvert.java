package com.barry.common.mvc.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.barry.common.core.bean.CommonPage;
import com.barry.common.core.bean.PageRequest;
import com.barry.common.core.bean.SqlOrderItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author barry chen
 * @date 2023/11/1 18:19
 */
public class PageConvert {

    /**
     * 把 PageRequest 转为 baomidou Page
     *
     * @param pageRequest
     * @param isSearchCount
     * @param <T>
     * @return
     */
    public static <T> Page<T> toPage(PageRequest pageRequest, boolean isSearchCount) {
        long current = pageRequest.getCurrent() == 0 ? 1 : pageRequest.getCurrent();
        long size = pageRequest.getSize() == 0 ? 10 : pageRequest.getSize();
        Page<T> page = new Page<T>(current, size, isSearchCount);
        if (pageRequest.getOrders() != null) {
            List<OrderItem> orderItemList = pageRequest.getOrders().stream()
                    .map(orderItem -> orderItem.isAsc() ? OrderItem.asc(orderItem.getColumn()) : OrderItem.desc(orderItem.getColumn()))
                    .collect(Collectors.toList());
            page.addOrder(orderItemList);
        }
        return page;
    }

    /**
     * 把baomidou Page 转为 CommonPage.
     *
     * @param page
     * @param fakeTotalFlag
     * @param <T>
     * @return
     */
    public static <T> CommonPage<T> toCommonPage(IPage<T> page, Boolean fakeTotalFlag) {
        CommonPage<T> commonPage = new CommonPage<T>(page.getSize(), page.getCurrent(), page.getTotal(), page.getRecords());
        if (fakeTotalFlag) {
            commonPage.setTotal(CommonPage.fakeTotal(page.getCurrent(), page.getSize(), page.getRecords().size()));
        }
        if (page.orders() != null && !page.orders().isEmpty()) {
            commonPage.setOrders(PageConvert.toSqlOrderItemList(page.orders()));
        }
        return commonPage;
    }

    /**
     * SqlOrderItem转为OrderItem
     *
     * @param sqlOrderItem
     * @return
     */
    public static OrderItem toOrderItem(SqlOrderItem sqlOrderItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(sqlOrderItem.getColumn());
        orderItem.setAsc(sqlOrderItem.isAsc());
        return orderItem;
    }

    /**
     * SqlOrderItemList转为OrderItemList
     *
     * @param sqlOrderItemList
     */
    public static List<OrderItem> toOrderItemList(List<SqlOrderItem> sqlOrderItemList) {
        return sqlOrderItemList.stream().map(PageConvert::toOrderItem).collect(Collectors.toList());
    }

    /**
     * OrderItem转为SqlOrderItem
     *
     * @param orderItem
     * @return
     */
    public static SqlOrderItem toSqlOrderItem(OrderItem orderItem) {
        SqlOrderItem sqlOrderItem = new SqlOrderItem();
        sqlOrderItem.setColumn(orderItem.getColumn());
        sqlOrderItem.setAsc(orderItem.isAsc());
        return sqlOrderItem;
    }

    /**
     * OrderItemList转为SqlOrderItemList
     *
     * @param orderItemList
     */
    public static List<SqlOrderItem> toSqlOrderItemList(List<OrderItem> orderItemList) {
        return orderItemList.stream().map(PageConvert::toSqlOrderItem).collect(Collectors.toList());
    }
}
