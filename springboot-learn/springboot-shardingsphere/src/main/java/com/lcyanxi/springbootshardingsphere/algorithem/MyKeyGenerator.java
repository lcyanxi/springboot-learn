package com.lcyanxi.springbootshardingsphere.algorithem;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

/**
 * @author lichang
 * @date 2021/1/21
 */
@Slf4j
public class MyKeyGenerator implements ShardingKeyGenerator {

    @Override
    public Comparable<?> generateKey() {
        long  id = System.currentTimeMillis()  + (long) (Math.random()*10);
        log.info("myKeyGenerator generateKey id:[{}]",id);
         return id;
    }

    @Override
    public String getType() {
        return "MY_KEY";
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
