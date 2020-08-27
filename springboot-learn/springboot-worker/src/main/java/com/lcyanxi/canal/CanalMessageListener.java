package com.lcyanxi.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import java.lang.reflect.InvocationTargetException;

/**
 * @author lichang
 * @date 2020/8/27
 */
@FunctionalInterface
public interface CanalMessageListener {

    /**
     * 消费同步的数据，以行为单位
     * @param entry 对应数据库的行
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void onMessage(CanalEntry.Entry entry) throws InvocationTargetException, IllegalAccessException;
}
