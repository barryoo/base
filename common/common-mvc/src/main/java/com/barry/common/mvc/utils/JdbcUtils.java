package com.barry.common.mvc.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author barry chen
 * @date 2020/12/10 4:41 下午
 */
public final class JdbcUtils {
    private static final int DEFAULT_CACHE_LIMIT = 256;

    private final static Map<String, ParsedSql> PARSED_SQL_CACHE =
            new LinkedHashMap<String, ParsedSql>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ParsedSql> eldest) {
                    return size() > DEFAULT_CACHE_LIMIT;
                }
            };

    private JdbcUtils() {
    }

    /**
     * 批量执行sql. 可用于insert/update/delete
     *
     * @param jdbcTemplate
     * @param sql
     * @param entityList
     * @return
     */
    public static int[] batchUpdate(NamedParameterJdbcTemplate jdbcTemplate, String sql, List<?> entityList) {
        BeanPropertySqlParameterSource[] notifyParamArray = new BeanPropertySqlParameterSource[entityList.size()];
        for (int i = 0; i < entityList.size(); i++) {
            notifyParamArray[i] = new BeanPropertySqlParameterSource(entityList.get(i));
        }
        return jdbcTemplate.batchUpdate(sql, notifyParamArray);
    }

    /**
     * 批量执行sql, 可用于insert/update/delete, 支持分批次更新
     *
     * @param jdbcTemplate jdbcTemplate实例
     * @param sql          要执行的sql
     * @param entityList   注入sql的元素集合
     * @param batchSize    批次数量
     * @return 每条数据的执行结果集合
     */
    public static <T> int[] batchUpdate(NamedParameterJdbcTemplate jdbcTemplate, String sql, List<T> entityList, int batchSize) {
        if (jdbcTemplate == null || StringUtils.isBlank(sql) || CollectionUtils.isEmpty(entityList) || batchSize <= 0) {
            return new int[]{0};
        }
        // 解析sql, 使用LinkedHashMap作为缓存
        ParsedSql parsedSql = getParsedSql(sql);
        // 提供转换格式后的sql和参数Setter
        PreparedStatementCreatorFactory pscf = getPreparedStatementCreatorFactory(parsedSql, new BeanPropertySqlParameterSource(entityList.get(0)));
        // 执行并返回结果
        int[][] batchUpdateResults = jdbcTemplate.getJdbcOperations().batchUpdate(pscf.getSql(), entityList, batchSize, (ps, argument) -> {
            // 因为这里argument是泛型, 不能直接往ps里赋值特定的字段, 只能使用预编译工厂来处理
            Object[] values = NamedParameterUtils.buildValueArray(parsedSql, new BeanPropertySqlParameterSource(argument), null);
            pscf.newPreparedStatementSetter(values).setValues(ps);
        });
        // 二维结果转一维
        int[] finalResult = new int[entityList.size()];
        int point = 0;
        for (int[] batchUpdateResult : batchUpdateResults) {
            for (int flag : batchUpdateResult) {
                finalResult[point++] = flag;
            }
        }
        return finalResult;
    }

    private static ParsedSql getParsedSql(String sql) {
        if (DEFAULT_CACHE_LIMIT <= 0) {
            return NamedParameterUtils.parseSqlStatement(sql);
        }
        synchronized (JdbcUtils.PARSED_SQL_CACHE) {
            return JdbcUtils.PARSED_SQL_CACHE.computeIfAbsent(sql, NamedParameterUtils::parseSqlStatement);
        }
    }

    private static PreparedStatementCreatorFactory getPreparedStatementCreatorFactory(
            ParsedSql parsedSql, SqlParameterSource paramSource) {

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        return new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
    }
}
