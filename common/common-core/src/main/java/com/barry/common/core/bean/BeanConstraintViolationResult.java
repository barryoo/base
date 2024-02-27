package com.barry.common.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Bean 参数验证结果</p>
 *
 * @author chenpeng
 * Create at March 12, 2019 at 14:34:43 GMT+8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeanConstraintViolationResult implements Serializable {

    private static final long serialVersionUID = -9026735732302420866L;

    private boolean valid;
    private List<String> errors;
}
