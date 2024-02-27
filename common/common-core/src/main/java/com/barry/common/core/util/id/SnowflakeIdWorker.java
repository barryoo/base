package com.barry.common.core.util.id;

/**
 * <p>雪花分布式 ID 生成器</p>
 *
 * @author chenpeng
 * Create at February 12, 2019 at 11:25:30 GMT+8
 */
public final class SnowflakeIdWorker implements IdWorker {
    public static final String ID_WORKER_NAME_SNOWFLAKE = "SNOWFLAKE";
    /**
     * 纪元 2018-04-20
     */
    private final long epoch = 1524153600000L;
    /**
     * 机器ID所占的位数
     */
    private final long workerIdBits = 6L;
    /**
     * 数据标识ID所占的位数
     */
    private final long dataCenterIdBits = 6L;
    /**
     * 支持的最大机器ID,结果是31
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);
    /**
     * 支持的最大数据标识ID,结果是31
     */
    private final long maxDataCenterId = ~(-1 << dataCenterIdBits);
    /**
     * 毫秒内序列在id中所占的位数
     */
    private final long sequenceBits = 12L;
    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 数据标识ID向左移18(12+6)位
     */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间戳向左移24(12+6+6)位
     */
    private final long timestampShift = sequenceBits + workerIdBits + dataCenterIdBits;
    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = ~(-1L << sequenceBits);
    /**
     * 数据标识ID（0～63）
     */
    private long dataCenterId;
    /**
     * 机器ID（0～63）
     */
    private long workerId;
    /**
     * 毫秒内序列（0～4095）
     */
    private long sequence;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    public SnowflakeIdWorker(){}

    public SnowflakeIdWorker(long dataCenterId, long workerId) {
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    @Override
    public IdWorker newInstance(long dataCenterId, long workerId) {
        return new SnowflakeIdWorker(dataCenterId, workerId);
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return snowflakeId
     */
    @Override
    public synchronized Long nextId() {
        long timestamp = generateTimeMills();
        //如果当前时间小于上一次ID生成的时间戳,说明系统时钟回退过,这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = nextMillis(lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        //移位并通过按位或运算拼到一起组成64位的ID
        return ((timestamp - epoch) << timestampShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    @Override
    public String getType() {
        return ID_WORKER_NAME_SNOWFLAKE;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long generateTimeMills() {
        return System.currentTimeMillis();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long nextMillis(long lastTimestamp) {
        long timestamp = generateTimeMills();
        while (timestamp <= lastTimestamp) {
            timestamp = generateTimeMills();
        }
        return timestamp;
    }
}
