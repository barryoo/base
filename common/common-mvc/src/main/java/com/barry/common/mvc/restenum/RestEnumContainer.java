package com.barry.common.mvc.restenum;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @date 2022/10/26 7:18 下午
 */
@Data
@AllArgsConstructor
public class RestEnumContainer {
    private Map<String, List<Map<String, Object>>> map;
}
