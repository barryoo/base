package com.barry.common.mvc.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.barry.common.core.bean.UnifiedUser;
import com.barry.common.core.bean.CommonPage;
import com.barry.common.mvc.utils.BaseModelUtils;
import com.barry.common.mvc.utils.PageConvert;

/**
 * 基础 service
 *
 * @param <M>
 * @param <T>
 * @author chenpeng
 * Create at December 2, 2018 at 14:05:49 GMT+8
 */
public abstract class AbstractCrudService<M extends BaseMapper<T>, T>
        extends ServiceImpl<M, T> implements CrudService<T> {

    @Override
    public void fillDefaultOperator(T model) {
        BaseModelUtils.fillDefaultOperator(model);
    }

    @Override
    public UnifiedUser getDefaultOperator() {
        return BaseModelUtils.getDefaultOperator();
    }

    @Override
    public void fillNecessaryOperatorProperty(T entity, UnifiedUser operator, boolean isUpdate) {
        BaseModelUtils.fillNecessaryOperatorProperty(entity, operator, isUpdate);
    }

    @Override
    public <E> CommonPage<E> toCommonPage(IPage<E> page) {
        return PageConvert.toCommonPage(page,false);
    }

}
