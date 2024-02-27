package com.barry.common.core.util.id;

import com.barry.common.core.exception.ApplicationException;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

import static com.barry.common.core.exception.SystemErrorCode.SYS_ID_WORKER_INIT_ERROR;

/**
 * <p>分布式 ID 生成器容器</p>
 *
 * @author chenpeng
 * Create at February 12, 2019 at 11:56:08 GMT+8
 */
public final class IdWorkerContainer {

    private static final Class<SnowflakeIdWorker> DEFAULT_ID_WORKER_CLASS = SnowflakeIdWorker.class;
    private static final String WORKERS_KEY_SEPARATOR = "_";

    private static final ConcurrentMap<String, IdWorker> WORKERS = Maps.newConcurrentMap();

    private final Long workerId;

    public IdWorkerContainer(Long workerId) {
        this.workerId = workerId;
    }

    /**
     * <p>获取给定 dataCenterId 对应的派号器，默认雪花</p>
     *
     * @param dataCenterId 数据中心 id
     * @return SnowflakeIdWorker
     * @author chenpeng
     * Create at February 12, 2019 at 13:16:50 GMT+8
     * @see IdWorker
     */
    public IdWorker getIdWorker(Long dataCenterId) {
        return getIdWorker(dataCenterId, DEFAULT_ID_WORKER_CLASS);
    }

    /**
     * <p>根据给定 dataCenterId 和类型获取对应的派号器，默认雪花</p>
     *
     * @param dataCenterId
     * @param idWorkerClass
     * @return IdWorker
     * @author chenpeng
     * Create at February 12, 2019 at 13:38:10 GMT+8
     * @see IdWorker
     */
    public IdWorker getIdWorker(Long dataCenterId, Class<? extends IdWorker> idWorkerClass) {

        IdWorker idWorker = null;

        if (idWorkerClass == null) {
            idWorkerClass = DEFAULT_ID_WORKER_CLASS;
        }

        if (dataCenterId != null) {
            String key = idWorkerClass.getSimpleName() + WORKERS_KEY_SEPARATOR + dataCenterId;
            idWorker = WORKERS.get(key);
            if (idWorker != null) {
                return idWorker;
            }

            IdWorker target = createIdWorker(dataCenterId, idWorkerClass);
            if (target != null) {
                WORKERS.putIfAbsent(key,
                        target);
                idWorker = target;
            }
        }

        return idWorker;
    }

    /**
     * 根据 IdWorkClass反射创建实例
     * @param dataCenterId
     * @param idWorkerClass
     * @return
     */
    private IdWorker createIdWorker(Long dataCenterId, Class<? extends IdWorker> idWorkerClass) {
        IdWorker idWorker = null;
        try {
            idWorker = idWorkerClass.newInstance();
            idWorker = idWorker.newInstance(dataCenterId, workerId);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(SYS_ID_WORKER_INIT_ERROR, e);
        }
        return idWorker;
    }
}
