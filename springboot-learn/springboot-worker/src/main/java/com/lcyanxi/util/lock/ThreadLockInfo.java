package com.lcyanxi.util.lock;

import lombok.Builder;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/8/3
 */
@Builder
@Data
public class ThreadLockInfo {
    /**
     * 线程
     */
    private Thread thread;

    /**
     * 锁的key
     */
    private String lockName;

    /**
     * 随机数，who lock, who release
     */
    private String seed;
}
