package com.barry.common.mvc.utils;

import com.barry.common.mvc.base.BaseModel;
import com.barry.common.core.bean.UnifiedUser;

import java.util.Date;
import java.util.Optional;

/**
 * @author barry chen
 * @date 2023/10/11 18:24
 */
public final class BaseModelUtils {
    private static final String DEFAULT_USER_ID = "0";
    private static final String DEFAULT_USER_NAME = "system";
    private static final UnifiedUser DEFAULT_OPERATOR = new UnifiedUser(DEFAULT_USER_ID, DEFAULT_USER_NAME);

    private BaseModelUtils() {
    }

    public static UnifiedUser getDefaultOperator() {
        return DEFAULT_OPERATOR;
    }

    public static <T> void fillDefaultOperator(T model) {
        Optional.ofNullable(model)
                .filter(m -> m instanceof BaseModel)
                .map(m -> (BaseModel) m)
                .ifPresent(m -> {
                    fillCreateOperator(m, null);
                    fillUpdateOperator(m, null);
                });
    }

    public static <T> void fillNecessaryOperatorProperty(T entity, UnifiedUser operator, boolean isUpdate) {
        if (entity == null) {
            return;
        }
        if (entity instanceof BaseModel) {
            BaseModel baseModel = (BaseModel) entity;
            if (isUpdate) {
                fillUpdateAt(baseModel);
                fillUpdateOperator(baseModel, operator);
            } else {
                fillCreateAt(baseModel);
                fillCreateOperator(baseModel, operator);
                fillUpdateAt(baseModel);
                fillUpdateOperator(baseModel, operator);
            }
        }
    }

    private static void fillUpdateOperator(BaseModel baseModel, UnifiedUser operator) {
        if (baseModel != null) {
            operator = (operator == null) ? DEFAULT_OPERATOR : operator;
            baseModel.setUpdateUserId(operator.getId().toString());
            baseModel.setUpdateUserName(operator.getName());
        }
    }

    private static void fillCreateOperator(BaseModel baseModel, UnifiedUser operator) {
        if (baseModel != null) {
            operator = (operator == null) ? DEFAULT_OPERATOR : operator;
            baseModel.setCreateUserId(operator.getId().toString());
            baseModel.setCreateUserName(operator.getName());
        }
    }

    private static void fillUpdateAt(BaseModel baseModel) {
        if (baseModel != null) {
            baseModel.setUpdateTime(new Date());
        }
    }

    private static void fillCreateAt(BaseModel baseModel) {
        if (baseModel != null) {
            baseModel.setCreateTime(new Date());
        }
    }

}
