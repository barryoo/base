package com.barry.common.core.util;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * <p>分片执行器工具类</p>
 *
 * @author chenpeng
 * Create at February 19, 2019 at 17:54:32 GMT+8
 */
public final class ShardingTaskExecutorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardingTaskExecutorUtils.class);

    /**
     * <p>默认每个分片执行的数据量</p>
     *
     * @author chenpeng
     * Create at February 19, 2019 at 18:33:17 GMT+8
     */
    private static final int DEFAULT_LIMIT = 2000;

    private ShardingTaskExecutorUtils() {
    }

    public static ShardingExecutor apply(Integer limit) {
        return new TaskShardingExecutor(limit);
    }

    /**
     * <p>分片执行器</p>
     *
     * @author chenpeng
     * Create at February 19, 2019 at 18:31:20 GMT+8
     */
    public interface ShardingExecutor {
        /**
         * <p>初始化</p>
         *
         * @author chenpeng
         * Create at February 19, 2019 at 19:40:25 GMT+8
         */
        void initialize(Map<String, Object> ctx);

        /**
         * <p>同步执行任务</p>
         * <p>需要注意的是：如果 breakOnError 为 false，即忽略异常继续执行，则事务不会被回滚</p>
         *
         * @author chenpeng
         * Create at February 19, 2019 at 18:31:54 GMT+8
         */
        void execute(ShardingTask task, boolean breakOnError);

        /**
         * <p>同步执行任务</p>
         *
         * @author chenpeng
         * Create at February 19, 2019 at 18:31:54 GMT+8
         */
        default void execute(ShardingTask task) {
            execute(task, false);
        }

        /**
         * <p>绑定参数</p>
         *
         * @author chenpeng
         * Create at February 19, 2019 at 19:36:49 GMT+8
         */
        default ShardingExecutor bind(Supplier<Map<String, Object>> supplier) {
            initialize(supplier == null ? null : supplier.get());
            return this;
        }

        /**
         * 绑定参数
         *
         * @author chenpeng
         * Create at February 19, 2019 at 19:57:07 GMT+8
         */
        default ShardingExecutor bind(String key, Object value) {
            return bind(() -> {
                Map<String, Object> item = Maps.newHashMap();
                item.put(key, value);
                return item;
            });
        }
    }

    /**
     * <p>分片任务</p>
     *
     * @author chenpeng
     * Create at February 19, 2019 at 20:09:23 GMT+8
     */
    @FunctionalInterface
    public interface ShardingTask {

        /**
         * 执行任务
         * @param index 当前分片index, 类似PageIndex
         * @param limit 每个分片可执行的最大数据量, 类似PageSize
         * @param context 上下文数据, 通过context控制入参与出参
         * @return 本次任务执行的真实数据量
         */
        int run(int index, int limit, Map<String, Object> context);
    }

    /**
     * <p>分片执行器默认实现</p>
     *
     * @author chenpeng
     * Create at February 19, 2019 at 18:32:16 GMT+8
     */
    private static class TaskShardingExecutor implements ShardingExecutor {

        private final Map<String, Object> ctx = Maps.newHashMap();

        private Integer limit;

        TaskShardingExecutor(Integer limit) {
            this.limit = Optional.ofNullable(limit).filter(i -> i != null && i > 0).orElse(DEFAULT_LIMIT);
        }

        @Override
        public void initialize(Map<String, Object> ctx) {
            if (ctx != null && ctx.size() > 0) {
                this.ctx.putAll(ctx);
            }
        }

        @Override
        public void execute(ShardingTask task, boolean breakOnError) {
            if (task != null) {
                boolean stop;
                int index = 0;
                do {
                    int count = 0;
                    try {
                        count = task.run(index, limit, ctx);
                        index++;
                    } catch (Throwable t) {
                        if (breakOnError) {
                            throw t;
                        }
                        LOGGER.error(t.getMessage(), t);
                        LOGGER.info("### 分片执行错误，但 breakOnError 为 {}，故仍然继续执行", breakOnError);
                    }
                    //如果本次任务执行数据量 小于 任务可执行的最大数据量, 表明是最后一页,可以停止了.
                    stop = count < limit;
                } while (!stop);
            }
        }
    }
}
