package com.barry.common.core.util;

import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

/**
 * Default sax ElementHandler: detaching elmenet
 */
public abstract class SaxElementHandler implements ElementHandler {
    @Override
    public void onStart(ElementPath arg) {
        arg.getCurrent().detach();
    }

    @Override
    public void onEnd(ElementPath arg) {
        try {
            parseElement(arg.getCurrent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            arg.getCurrent().detach();
            arg = null;
        }
    }

    protected abstract void parseElement(Element ele) throws Exception;
}
