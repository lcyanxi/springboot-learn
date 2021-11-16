package com.lcyanxi.springbootmanage;

import org.junit.Test;

import java.util.Random;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2021/11/16/10:15 上午
 */
public class UtilTest {

    @Test
    public void test(){
        int index = 0;
        while (index ++ < 100){
            int score = 6;
            Random random = new Random();
            Double newScore = random.doubles((score * 0.8), (score * 1.2)).findFirst().orElse(0);
            System.out.println(newScore.intValue());
        }
    }
}
