package com.lcyanxi.threadPool;

import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/29
 */
@Data
public class ThreadPoolConfig {

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize;

    /**
     * 队列容量
     */
    private Integer queueCapacity;


}
