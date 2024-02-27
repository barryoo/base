package com.barry.auto.transmitter.interceptor;

import com.barry.auto.transmitter.core.HolderContext;
import com.barry.auto.transmitter.core.model.Holder;
import com.barry.auto.transmitter.core.model.Holders;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TransmitterHttpRequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Map<Class<? extends Holder>, Holder> holderConfig = HolderContext.getHolderConfig();
        Holders holders = new Holders();
        for (Holder holder : holderConfig.values()) {
            String value = request.getHeader(holder.getHeaderKey());
            if (value == null) {
                continue;
            }
            value = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
            holder.setObject(holder.fromString(value));
            holders.addHolder(holder);
        }

        if(holders.getAllHolder().size() > 0){
            HolderContext.removeLocalHolders();
            HolderContext.setLocalHolders(holders);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        HolderContext.removeLocalHolders();
        return;
    }
}
