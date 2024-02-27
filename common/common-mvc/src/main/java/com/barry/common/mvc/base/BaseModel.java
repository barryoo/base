package com.barry.common.mvc.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>基础模型</p>
 *
 * @author chenpeng
 * Create at January 2, 2019 at 15:35:46 GMT+8
 */
@Data
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = 6107459102213712827L;

    private Date createTime;
    private Date updateTime;
    private String createUserId;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;

}
