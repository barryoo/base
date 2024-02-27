package com.barry.auto.transmitter.interceptor;

import com.barry.auto.transmitter.core.HolderContext;
import com.barry.auto.transmitter.core.model.Holder;
import com.barry.auto.transmitter.core.model.Holders;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class TransmitterFeignClientInterceptor implements RequestInterceptor {

    @SneakyThrows
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Holders holders = getOrInitHolders();
        for (Holder holder : holders.getAllHolder()) {
                requestTemplate.header(holder.getHeaderKey(), URLEncoder.encode(holder.toString(holder.getObject()), StandardCharsets.UTF_8.name()));
        }
    }

    private Holders getOrInitHolders(){
        Holders holders = HolderContext.getLocalHolders();
        if (holders == null) {
            holders = new Holders();
        }
        /*
        检查是否所有Holder都已经存在. 如果不存在,则需要初始化.
        先对比size是否满足, 如果满足则认为所有Holder都存在. 如果size不同, 则需要根据holderConfig对缺失的Holder进行初始化.
        */
        if (holders.getAllHolder().size() == HolderContext.getHolderConfig().size()) {
            return holders;
        }
        for (Class<? extends Holder> c : HolderContext.getHolderConfig().keySet()) {
            Holder holder = holders.getHolder(HolderContext.getHolderConfig().get(c).getHeaderKey());
            if (holder == null) {
                holder = BeanUtils.instantiateClass(c);
                holder.initObject();
                if(Objects.nonNull(holder.getObject())){
                    holders.addHolder(holder);
                }
            }
        }
        HolderContext.setLocalHolders(holders);
        return holders;
    }

}
