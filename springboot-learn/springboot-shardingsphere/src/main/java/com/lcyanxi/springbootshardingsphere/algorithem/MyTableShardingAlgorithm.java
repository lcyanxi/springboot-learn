package com.lcyanxi.springbootshardingsphere.algorithem;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

/**
 * @author lichang
 * @date 2021/1/19
 */
@Slf4j
public class MyTableShardingAlgorithm implements RangeShardingAlgorithm<Long>, PreciseShardingAlgorithm<Long> {
    /**
     * 支持范围查询 select * from course where cid between 1 and 100;
     * @param availableTargetNames 可利用的真实表
     * @param rangeShardingValue 分片值
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> rangeShardingValue) {
        //select * from course where cid between 1 and 100;
        Long upperVal = rangeShardingValue.getValueRange().upperEndpoint();//100
        Long lowerVal = rangeShardingValue.getValueRange().lowerEndpoint();//1
        log.info("rangeShardingAlgorithm availableTargetNames:[{}],rangeShardingValue:[{}]",availableTargetNames,rangeShardingValue);

        String logicTableName = rangeShardingValue.getLogicTableName();
        return Arrays.asList(logicTableName+"_1",logicTableName+"_2");
    }

    /**
     * 支持精确查找 select * from course where cid = ''
     * @param availableTargetNames 可选真实表
     * @param preciseShardingValue 分片值
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        String cid = preciseShardingValue.getColumnName();
        Long cidValue = preciseShardingValue.getValue();
        //实现 course_$->{cid%2+1)
        BigInteger shardingValueB = BigInteger.valueOf(cidValue);
        BigInteger resB = (shardingValueB.mod(new BigInteger("2"))).add(new BigInteger("1"));
        String key = logicTableName+"_"+resB;
        if(availableTargetNames.contains(key)){
            return key;
        }
        //couse_1, course_2
        throw new UnsupportedOperationException("route "+ key +" is not supported ,please check your config");
    }
}
