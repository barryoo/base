package com.barry.common.core.util.id;

import com.barry.common.core.util.Convert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author barry chen
 * @date 2022/9/14 15:56
 */
@Slf4j
public class IdWorkerContainerTest {

    public static final long workerId = 1L;
    public static final long dataCenterId = 1L;

    @Test
    public void getIdWorkerTest() {
        IdWorkerContainer idWorkerContainer = new IdWorkerContainer(workerId);
        //生成默认的idWorker应该是SnowFlake
        IdWorker idWorkerDefault = idWorkerContainer.getIdWorker(dataCenterId);
        Assert.assertSame(idWorkerDefault.getClass().getName(), SnowflakeIdWorker.class.getName());

        //生成指定的IdWorker.
        IdWorker idWorkerSnowFlake = idWorkerContainer.getIdWorker(dataCenterId, SnowflakeIdWorker.class);
        Assert.assertSame(idWorkerSnowFlake.getClass().getName(), SnowflakeIdWorker.class.getName());
    }

    @Test
    public void nextIdTest(){
        IdWorkerContainer idWorkerContainer = new IdWorkerContainer(50l);
        IdWorker idWorker = idWorkerContainer.getIdWorker(dataCenterId);
        for (int i=0;i<2;i++){
            idWorker.nextId();
        }
    }

    @Test
    public void nextShortIdTest(){
        IdWorkerContainer idWorkerContainer = new IdWorkerContainer(50l);
        IdWorker idWorker = idWorkerContainer.getIdWorker(dataCenterId);
        for (int i=0;i<2;i++){
            log.info(Convert.toBase(idWorker.nextId(), Convert.Base.BASE_36));
        }

    }

}
