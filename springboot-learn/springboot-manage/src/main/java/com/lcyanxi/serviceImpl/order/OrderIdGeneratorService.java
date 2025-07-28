package com.lcyanxi.serviceImpl.order;

/**
 * @author chang.li
 * @version 1.0
 * @date 2025/7/28
 */
public class OrderIdGeneratorService {
    private SnowflakeIdGenerator snowflakeIdGenerator;
    private RedisOrderNumberGenerator redisOrderNumberGenerator;

    public OrderIdGeneratorService(long workerId, long datacenterId) {
        this.snowflakeIdGenerator = new SnowflakeIdGenerator(workerId, datacenterId);
        this.redisOrderNumberGenerator = new RedisOrderNumberGenerator();
    }

    public String generateOrderNumber(String businessId) {
        // 使用雪花算法生成部分订单号
        long snowflakeId = snowflakeIdGenerator.nextId();
        // 使用Redis生成序列号
        String redisSeq = redisOrderNumberGenerator.generateOrderNumber(businessId);

        return snowflakeId + "-" + redisSeq;
    }
}
