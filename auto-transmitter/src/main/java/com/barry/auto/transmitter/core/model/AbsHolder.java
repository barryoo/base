package com.barry.auto.transmitter.core.model;

import com.barry.common.spring.util.JsonUtils;

import java.lang.reflect.ParameterizedType;

public abstract class AbsHolder<T> implements Holder {

    private final static String HEAD_KEY_PREFIX = "TRANSMITTER-";

    protected T t;

    @Override
    public String getHeaderKey() {
        return HEAD_KEY_PREFIX + getSubKey();
    }

    public abstract String getSubKey();

    @Override
    public abstract void initObject();

    @Override
    public Object getObject() {
        return doGetObject();
    }

    @Override
    public void setObject(Object o) {
        t = (T) o;
    }

    public T doGetObject() {
        return t;
    }

    @Override
    public Object fromString(String value) {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return JsonUtils.fromJson(value, clazz);
    }

    @Override
    public String toString(Object o) {
        return JsonUtils.toJson(o);
    }
}
