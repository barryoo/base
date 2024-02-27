package com.barry.auto.transmitter.core;

import com.barry.auto.transmitter.core.model.Holder;
import com.barry.auto.transmitter.core.model.Holders;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class HolderContext {

    private static ThreadLocal<Holders> holdersThreadLocal = new ThreadLocal<>();
    private static Map<Class<? extends Holder>, Holder> holderConfig = new HashMap<>(4);

    private HolderContext() {
    }

    /**
     * 获取当前线程中,指定类型的传输对象
     *
     * @param clazz Holder类型
     * @return 传输对象
     */
    public static Object get(Class<? extends Holder> clazz) {
        String headerKey = holderConfig.get(clazz).getHeaderKey();
        return Optional.ofNullable(HolderContext.getLocalHolders())
                .map(h -> h.getHolder(headerKey))
                .map(h -> h.getObject()).orElse(null);
    }

    public static void removeLocalHolders() {
        holdersThreadLocal.remove();
    }

    public static void setLocalHolders(Holders holders) {
        holdersThreadLocal.set(holders);
    }

    public static Holders getLocalHolders() {
        return holdersThreadLocal.get();
    }

    public static Map<Class<? extends Holder>, Holder> getHolderConfig() {
        return holderConfig;
    }

    public static void config(List<Class<? extends Holder>> holderClassList) {
        for (Class<? extends Holder> cla : holderClassList) {
            Holder holder = BeanUtils.instantiateClass(cla);
            holderConfig.put(holder.getClass(), holder);
        }
    }

}
