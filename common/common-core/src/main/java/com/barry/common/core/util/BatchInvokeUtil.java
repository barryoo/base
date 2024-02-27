package com.barry.common.core.util;

import com.barry.common.core.exception.BusinessException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>BatchInvokeUtil</p>
 * description:
 *
 * Create time 2021-11-17 16:14:37
 */
public class BatchInvokeUtil {
    /**
     * 提供批量参数, 分批调用
     *
     * @param paramList 批量参数
     * @param size      批次大小
     * @param invoke    调用函数
     * @param <T>       入参类型
     */
    public static <T> void batchParamInvoke(List<T> paramList, int size, Consumer<List<T>> invoke) {
        if (CollectionUtils.isEmpty(paramList)) {
            return;
        }

        List<List<T>> partition = Lists.partition(paramList, size);
        partition.forEach(invoke);
    }

    /**
     * 提供批量参数, 分批调用并遍历收集为List
     * <p>
     * 1.如果要目标方法接收参数不是集合, 可以把size设置为1进行调用和收集
     * BatchInvokeUtil.batchParamInvokeCollectList(paramList, 1, it -> getSkuComboList(it.get(0)));
     * 2.虽然方法只提供了paramList一个入参的参数, 但是通过Function的函数体, 可以灵活的接收多个入参, 以及处理返回的结果
     * <pre>
     * {@code
     * String otherParam = "";
     * BatchInvokeUtil.batchParamInvokeCollectList(paramList, 100, it -> {
     *      List<Dto> dtoList = service.query(otherParam, it)
     *      return dtoList.stream.map(Dto::getId).collect(Collectors.toList());
     * });
     * }
     * <pre/>
     *
     * @param paramList 批量参数
     * @param size      批次大小
     * @param invoke    调用函数
     * @param <T>       入参类型
     * @param <R>       返参类型
     * @return 返回多批调用汇总后的结果
     */
    public static <T, R> List<R> batchParamInvokeCollectList(List<T> paramList, int size, Function<List<T>, List<R>> invoke) {
        if (CollectionUtils.isEmpty(paramList)) {
            throw new BusinessException("paramList is empty");
        }
        List<List<T>> partition = Lists.partition(paramList, size);
        return partition.stream()
                .flatMap(invoke.andThen(List::stream))
                .collect(Collectors.toList());
    }

    /**
     * 提供批量参数, 分批调用并收集, 转换为Map
     * 使用规则参考batchParamInvokeCollectList
     *
     * @param paramList 批量参数
     * @param size      批次大小
     * @param invoke    调用函数
     * @param getKey    group by的key, 例如: Dto::getId()
     * @param <T>       入参类型
     * @param <K>       Map key类型
     * @param <R>       Map value类型
     * @return 返回多批调用汇总并group by后的Map, 结果为Map<K, R>, 要求K在返回结果中唯一, 否则会抛出异常
     */
    public static <T, K, R> Map<K, R> batchParamInvokeCollectMap(List<T> paramList, int size, Function<List<T>, List<R>> invoke, Function<R, K> getKey) {
        if (CollectionUtils.isEmpty(paramList)) {
            throw new BusinessException("paramList is empty");
        }
        List<List<T>> partition = Lists.partition(paramList, size);
        return partition.stream()
                .flatMap(invoke.andThen(List::stream))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(getKey, Function.identity()));

    }

    /**
     * 提供批量参数, 分批调用并收集, 再group by为Map
     * 使用规则参考batchParamInvokeCollectList
     *
     * @param paramList 批量参数
     * @param size      批次大小
     * @param invoke    调用函数
     * @param getKey    group by的key, 例如: Dto::getId()
     * @param <T>       入参类型
     * @param <K>       Map key类型
     * @param <R>       Map value类型
     * @return 返回多批调用汇总并group by后的Map, 结果为Map<K, List<R>>, List<R>的元素数量取决于key是否唯一
     */
    public static <T, K, R> Map<K, List<R>> batchParamInvokeCollectGroupingByMap(List<T> paramList, int size, Function<List<T>, List<R>> invoke, Function<R, K> getKey) {
        if (CollectionUtils.isEmpty(paramList)) {
            throw new BusinessException("paramList is empty");
        }
        List<List<T>> partition = Lists.partition(paramList, size);
        return partition.stream()
                .flatMap(invoke.andThen(List::stream))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(getKey));
    }
}
