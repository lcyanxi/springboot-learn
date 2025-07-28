package com.lcyanxi.serviceImpl.order;

/**
 * @author chang.li
 * @version 1.0
 * @date 2025/7/28
 */
public class SnowflakeIdGenerator {

    private final long workerId;
    private final long datacenterId;
    private final long sequence;

    private final long twepoch = 1288834974657L; // 基准时间
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;
    private long sequenceId = 0L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > (1L << workerIdBits) - 1 || workerId < 0) {
            throw new IllegalArgumentException("workerId out of range");
        }
        if (datacenterId > (1L << datacenterIdBits) - 1 || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId out of range");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = 0L;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        if (timestamp == lastTimestamp) {
            sequenceId = (sequenceId + 1) & sequenceMask;
            if (sequenceId == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequenceId = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequenceId;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
