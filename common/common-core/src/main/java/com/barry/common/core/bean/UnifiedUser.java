package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>系统统一用户</p>
 *
 * @author chenpeng
 * Create at January 2, 2019 at 19:34:29 GMT+8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UnifiedUser implements Serializable {

    private static final long serialVersionUID = -2736561754154136210L;

    @NotNull(message = "operator 的 id 不能为 null")
    private String id;

    @NotBlank(message = "operator 的 name 不能为空")
    private String name;

    public static UnifiedUser create(String id, String name) {
        return new UnifiedUser(id, name);
    }
}
