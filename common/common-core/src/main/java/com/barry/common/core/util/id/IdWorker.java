package com.barry.common.core.util.id;

/**
 * <p>ID 派发器</p>
 *
 * @author chenpeng
 * Create at February 12, 2019 at 13:25:58 GMT+8
 */
public interface IdWorker {

    IdWorker newInstance(long dataCenterId, long workerId);

    /**
     * <p>获取下一个 id</p>
     *
     * @return Long 64 位 id
     * @author chenpeng
     * Create at February 12, 2019 at 13:26:41 GMT+8
     */
    Long nextId();

    /**
     * <p>获取当前 idworker 的实现类型</p>
     *
     * @return IdWorkerTypeEnum
     * @author chenpeng
     * Create at February 12, 2019 at 13:48:51 GMT+8
     */
    String getType();

}
