package com.zhuofan.workplan.ai.util;

import lombok.Getter;
import lombok.Setter;

/**
 * description  雪花算法
 *  主键生成
 */
@Getter
@Setter
public class SnowFlakeGenerator {

    /**
     * 生成的唯一键的长度(bit)
     */
    private long length = 64;
    /**
     * 时间戳长度
     */
    private long timestampLength = 41;
    /**
     * 机器位最大长度 datacenterId(5) + workId(5)
     */
    private long datacenterIdLength = 5;
    private long workerIdLength = 5;
    /**
     * 序列位长度
     */
    private long sequenceLength = 12;
    /**
     * 机器Id左移位数
     */
    private long workIdLShift = sequenceLength;
    /**
     * 数据中心Id左移位数
     */
    private long datacenterIdLShift = sequenceLength + workerIdLength;
    /**
     * 时间戳左偏移位数
     */
    private long timestampLShift = sequenceLength + workerIdLength + datacenterIdLength;

    // 1、左移后面补位的都是0，所以需要取反，才能得到000011111这种形式以进行位运算
    // 2、既然需要取反，那么为了保证前面的字节都是0，那么它原来就必须是1，那么就是负数进行左移
    private long maxDatacenterId = ~(-1L << datacenterIdLShift);
    private long maxWorkId = ~(-1L << workIdLShift);
    private long maxSequence = ~(-1L << sequenceLength);

    private long datacenterId, workId, sequence;

    private final long startTimestamp = 1711364268130L;

    /**
     * 为了在启动时重置
     */
    private long lastTimestamp = -1L;

    public SnowFlakeGenerator(long datacenterId, long workId, long sequence) {
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId范围为[0~" + maxDatacenterId + "]");
        }
        if (workId > maxWorkId || workId < 0) {
            throw new IllegalArgumentException("workId范围为[0~" + maxWorkId + "]");
        }
        if (sequence > maxSequence || sequence < 0) {
            throw new IllegalArgumentException("sequence范围为[0~" + maxSequence + "]");
        }
        this.datacenterId = datacenterId;
        this.workId = workId;
        this.sequence = sequence;
    }

    /**
     * 生成下一个key
     * @return 雪花算法 key
     */
    public synchronized long generateNextId() {
        long currentTimestamp = currentTimestamp(lastTimestamp);

        // 并发下可能是同一毫秒，添加序列号sequence
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // sequence超过最大值，按位‘与’后为0，当前时间戳序列已满，下一个时间戳
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                currentTimestamp = currentTimestamp(lastTimestamp);
            }
        }else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - startTimestamp) << timestampLShift) |
                (datacenterId << datacenterIdLShift) |
                (workId << workIdLShift) |
                sequence;
    }

    private long currentTimestamp(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("新的时间戳在旧时间戳之前");
        }
        return timestamp;
    }

}
