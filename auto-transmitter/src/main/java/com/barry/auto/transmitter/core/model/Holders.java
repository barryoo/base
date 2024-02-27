package com.barry.auto.transmitter.core.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Holders {

    private final Map<String, Holder> holderMap = new HashMap<>();

    public Holders() {
    }

    public void addHolder(Holder holder) {
        this.holderMap.put(holder.getHeaderKey(), holder);
    }

    public Holder getHolder(String headerKey) {
        return this.holderMap.get(headerKey);
    }

    public Collection<Holder> getAllHolder() {
        return this.holderMap.values();
    }

}
