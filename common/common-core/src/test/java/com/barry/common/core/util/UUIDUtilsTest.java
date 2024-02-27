package com.barry.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.IntStream;

/**
 * @author barry chen
 * @date 2022/11/23 09:42
 */

@Slf4j
public class UUIDUtilsTest {
    @Test
    public void testShortUUID() {
        log.info(UUIDUtils.shortUUID62());
        log.info(UUIDUtils.shortUUID36());
    }

    @Test
    public void testUUID() {
        IntStream.range(0,10000).forEach(i -> {
            log.info(UUIDUtils.uuid());
        });
    }
}
