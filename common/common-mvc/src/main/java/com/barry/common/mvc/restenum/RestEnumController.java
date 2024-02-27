package com.barry.common.mvc.restenum;

import com.barry.common.core.bean.ApiResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @date 2022/10/27 9:28 上午
 */
@RestController
@RequestMapping("/restEnum")
public class RestEnumController {

    @Resource(name = "restEnum")
    @Lazy
    private RestEnumContainer enumMeta;

    @GetMapping("/{enumClassName}")
    public ApiResult<List<Map<String, Object>>> getEnum(@PathVariable String enumClassName) {
        return ApiResult.success(enumMeta.getMap().get(enumClassName));
    }

}
