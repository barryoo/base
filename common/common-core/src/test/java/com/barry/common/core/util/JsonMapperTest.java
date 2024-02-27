package com.barry.common.core.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author barry chen
 * @date 2022/11/8 17:20
 */
@Log
public class JsonMapperTest {

    @Test
    public void testNullSerialize(){
        JsonMapper jsonMapper = new JsonMapper();
        TestBean testBean1 = new TestBean(null, null, null,null,null,null,null,null);
        log.info(jsonMapper.toJson(testBean1));
        TestBean testBean2 = new TestBean("a", 1, false, null, Lists.newArrayList("b","c") , Sets.newHashSet(4,5,6), ImmutableMap.of("a",1L, "b",2L), null);
        testBean2.setSubTestBean(testBean1);
        testBean2.setTestBeanList(Lists.newArrayList(testBean1));
        log.info(jsonMapper.toJson(testBean2));
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class TestBean {
        private String name;
        private Integer age;
        private Boolean aBoolean;
        private TestBean subTestBean;
        private List<String> list;
        private Set<Integer> set;
        private Map<String, Long> map;
        private List<TestBean> testBeanList;
    }
}
