package com.barry.common.mvc.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.barry.common.core.bean.UnifiedUser;
import com.barry.common.core.bean.CommonPage;

/**
 * 基础 crud service
 *
 * @param <T>
 * @author chenpeng
 * Create at December 2, 2018 at 14:37:34 GMT+8
 * @see AbstractCrudService
 */
public interface CrudService<T> extends IService<T> {

    /**
     * <p>填充默认操作用户</p>
     *
     * @author chenpeng
     * Create at January 2, 2019 at 19:32:38 GMT+8
     */
    void fillDefaultOperator(T model);

    /**
     * <p>获取默认操作用户</p>
     *
     * @author chenpeng
     * Create at January 2, 2019 at 19:42:55 GMT+8
     */
    UnifiedUser getDefaultOperator();

    /**
     * <p>填充必要参数</p>
     *
     * @param model
     * @param operator
     * @param isUpdate
     * @author chenpeng
     * Create at March 13, 2019 at 18:01:52 GMT+8
     */
    void fillNecessaryOperatorProperty(T model, UnifiedUser operator, boolean isUpdate);

    /**
     * new common page
     *
     * @param page page
     * @param <E>  object type
     * @return common page
     */
    <E> CommonPage<E> toCommonPage(IPage<E> page);

}
