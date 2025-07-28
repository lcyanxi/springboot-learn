package com.lcyanxi.serviceImpl.order;

import redis.clients.jedis.Jedis;

/**
 * @author chang.li
 * @version 1.0
 * @date 2025/7/28
 */
public class RedisOrderNumberGenerator {

    private static final String REDIS_KEY_PREFIX = "order:seq:";
    private Jedis jedis;

    public RedisOrderNumberGenerator() {
        Jedis jedis1 = new Jedis("127.0.0.1", 6379);
        jedis1.auth("abcd13579");
        this.jedis = jedis1;
    }

    public String generateOrderNumber(String businessId) {
        String date = getCurrentDate();
        String redisKey = REDIS_KEY_PREFIX + businessId + ":" + date;

        // 生成自增序列号
        long seq = jedis.incr(redisKey);

        // 构造订单号
        return date + "-" + businessId + "-" + String.format("%04d", seq);
    }

    private String getCurrentDate() {
        // 返回当前日期，如20210928
        return new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    }
}
