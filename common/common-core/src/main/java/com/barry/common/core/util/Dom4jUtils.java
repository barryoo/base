package com.barry.common.core.util;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Dom4j 通用工具类
 */
public final class Dom4jUtils {

    private Dom4jUtils() {
    }

    public static SAXReader initSaxReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        SAXReader reader = new SAXReader();
        reader.setXMLReader(factory.newSAXParser().getXMLReader());
        reader.setEncoding("UTF-8");
        reader.setDefaultHandler(new SaxElementHandler() {
            @Override
            protected void parseElement(Element ele) throws Exception {
            }
        });
        return reader;
    }

    public static BigDecimal selectNodeDecimal(Node doc, String xpathExpression) {
        return Optional.ofNullable(selectNodeText(doc, xpathExpression)).filter(StringUtils::isNotBlank).map(BigDecimal::new).orElse(BigDecimal.valueOf(0));
    }

    public static String selectNodeText(Node doc, String xpathExpression) {
        return selectNodeText(doc, xpathExpression, "");
    }

    public static String selectNodeText(Node doc, String xpathExpression, String orElseVal) {
        return Optional.ofNullable(doc).map(d -> {
            return doc.selectSingleNode(xpathExpression);
        }).map(Node::getText).orElse(orElseVal);
    }

    public static Integer selectNodeInteger(Node doc, String xpathExpression) {
        return Optional.ofNullable(selectNodeText(doc, xpathExpression)).filter(StringUtils::isNotBlank).map(Integer::valueOf).orElse(0);
    }

    public static Integer selectNodeAttrInteger(Element element, String attrName) {
        return Optional.ofNullable(selectNodeAttrText(element, attrName, "")).filter(StringUtils::isNotBlank).map(Integer::valueOf).orElse(0);
    }

    public static String selectNodeAttrText(Element element, String attrName, String orElseVal) {
        return Optional.ofNullable(element.attribute(attrName)).map(Attribute::getText).orElse(orElseVal);
    }

    public static String getElementText(Element element, String eleName) {
        return getElementText(element, eleName, null);
    }

    public static String getElementText(Element element, String eleName, String defaultVal) {
        return element == null ? defaultVal : (element.element(eleName) == null ? defaultVal : StringUtils.trim(element.elementText(eleName)));
    }
}
