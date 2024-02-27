package com.barry.common.core.util;

import junit.framework.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author barry chen
 * @date 2023/5/23 15:49
 */
@Slf4j
public class BeanMapperUtilsTest extends TestCase {

    public void testCopy() {
        Copy copy = new Copy(1, null, true, new Date());
        Copy copy2 = new Copy(2, "asdf", null, null);
        BeanMapperUtils.copy(copy, copy2);
        log.info("copy2:{}", copy2);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Copy{
        private Integer id;
        private String name;
        private Boolean flag;
        private Date createTime;
    }

}
