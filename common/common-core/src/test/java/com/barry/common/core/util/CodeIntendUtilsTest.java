package com.barry.common.core.util;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author barry chen
 * @date 2023/5/5 16:56
 */
@Slf4j
public class CodeIntendUtilsTest extends TestCase {

    @Test
    public void testUnIntendHtml() {
        String html = "asdf\n" +
                "\n" +
                "< body >\n" +
                "\n" +
                "    sdfasdf \n" +
                "\n" +
                "    </  body >  \n" +
                "  <  td  >  sdf  </  td  >   \n" +
                "  \n" +
                "  <  td  />  \n" +
                "  asdf\n" +
                "  <  td  >  sdf  </  td  >   \n";
        log.info(CodeIntendUtils.unIntendHtml(html));
    }
}
