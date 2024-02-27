/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.barry.common.mvc.config.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.barry.common.spring.util.SpringEnvUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * 由于mybatis自带的Configuration不允许添加已存在的MappedStatement, 且mybatisplus中的MybatisConfiguration也不允许,
 * 所以重写addMappedStatement方法
 *
 * @author chen
 * @since 2020-07-22
 */
public class DefaultMybatisConfiguration extends MybatisConfiguration {
    private static final Log logger = LogFactory.getLog(MybatisConfiguration.class);

    /**
     * MybatisPlus 加载 SQL 顺序：
     * <p>1、加载XML中的SQL</p>
     * <p>2、加载sqlProvider中的SQL</p>
     * <p>3、xmlSql 与 sqlProvider不能包含相同的SQL</p>
     * <p>调整后的SQL优先级：xmlSql > sqlProvider > curdSql</p>
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        logger.debug("addMappedStatement: " + ms.getId());
        String profile = SpringEnvUtils.getProfile();
        if (StringUtils.isNotBlank(profile) && "dev".equalsIgnoreCase(profile)) {
            if (super.mappedStatements.containsKey(ms.getId())) {
                super.mappedStatements.remove(ms.getId());
            }
            mappedStatements.put(ms.getId(), ms);
            return;
        }
        if (mappedStatements.containsKey(ms.getId())) {
            /*
             * 说明已加载了xml中的节点； 忽略mapper中的SqlProvider数据
             */
            logger.error("mapper[" + ms.getId() + "] is ignored, because it exists, maybe from xml file");
            return;
        }
        mappedStatements.put(ms.getId(), ms);
    }

}
