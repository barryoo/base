package com.barry.auto.transmitter.core.model;

import java.io.Serializable;

public interface Holder extends Serializable {
    /**
     * 获取http header key
     *
     * @return
     */
    String getHeaderKey();

    /**
     * 初始化传输对象
     * <p>
     * 当getObject()为空时,会调用该方法
     */
    void initObject();

    /**
     * 获取传输对象
     *
     * @return
     */
    Object getObject();

    /**
     * 设置传输对象
     * <p>
     * 从request.header中获取到传输对象后,调用该方法,设置到holder中.
     *
     * @param o
     */
    void setObject(Object o);

    /**
     * 反序列化,从request.header中获取到传输对象后,调用该方法,获取Object
     *
     * @param value
     * @return
     */
    Object fromString(String value);

    /**
     * 序列化,把对象放入header之前,调用该方法,或许传输对象的序列化字符串.
     *
     * @param o
     * @return
     */
    String toString(Object o);

}
