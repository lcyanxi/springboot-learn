package com.lcyanxi.springbootshardingsphere.algorithem;

import com.google.common.collect.Range;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

/**
 * @author lichang
 * @date 2021/1/19
 */
public class MyComplexTableShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {
    @Override
    public Collection<String> doSharding(Collection<String> collection, ComplexKeysShardingValue<Long> shardingValue) {
        Range<Long> cidRange = shardingValue.getColumnNameAndRangeValuesMap().get("cid");
        Collection<Long> userIdCol = shardingValue.getColumnNameAndShardingValuesMap().get("user_id");

        Long upperVal = cidRange.upperEndpoint();
        Long lowerVal = cidRange.lowerEndpoint();

        List<String> res = new ArrayList<>();

        for(Long userId: userIdCol){
            //course_{userID%2+1}
            BigInteger userIdB = BigInteger.valueOf(userId);
            BigInteger target = (userIdB.mod(new BigInteger("2"))).add(new BigInteger("1"));

            res.add(shardingValue.getLogicTableName()+"_"+target);
        }
        return res;
    }

}
