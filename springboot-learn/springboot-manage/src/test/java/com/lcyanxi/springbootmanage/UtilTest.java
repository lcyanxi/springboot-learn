package com.lcyanxi.springbootmanage;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2021/11/16/10:15 上午
 */
@Slf4j
public class UtilTest {
    private static final long LAST_CONNECTION_TIMEOUT = TimeUnit.MINUTES.toSeconds(10);
    @Test
    public void test() {
        int index = 0;
        while (index++ < 100) {
            int score = 6;
            long num = 19;
            log.info("LAST_CONNECTION_TIMEOUT:{}",LAST_CONNECTION_TIMEOUT);
            int rang = BigDecimal.valueOf(num).multiply(BigDecimal.valueOf(0.05)).intValue();
            int newRange = BigDecimal.valueOf(num).multiply(BigDecimal.valueOf(0.05))
                    .setScale(0, RoundingMode.HALF_UP).intValue();
            log.info("num:{},rang:{},newRange:{}", (num - score),rang,newRange);
            Random random = new Random();
            Double newScore = random.doubles((score * 0.8), (score * 1.2)).findFirst().orElse(0);
            log.info("old:{},new:{}", newScore, Math.floor(newScore));
        }
    }
}
