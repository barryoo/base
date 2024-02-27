package com.barry.common.mvc.loadbalance.rule;

import com.barry.auto.transmitter.core.model.Holder;

public class VersionHolder implements Holder {
    private static final String HEAD_KEY = "version";

    private String t;

    @Override
    public String getHeaderKey() {
        return HEAD_KEY;
    }

    @Override
    public void initObject() {
    }

    @Override
    public Object getObject() {
        return t;
    }

    @Override
    public void setObject(Object o) {
        t = (String) o;
    }

    @Override
    public Object fromString(String value) {
        return value;
    }

    @Override
    public String toString(Object o) {
        return (String) o;
    }
}
