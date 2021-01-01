package com.lcyanxi.designPatterns.strategy;

/**
 * @author lichang
 * @date 2021/1/1
 */
public class StrategyDemoTest {
    public static void main(String[] args) {
        StrategyContext context = new StrategyContext(new BluePenImpl()); // 使用蓝色笔来画
        context.executeDraw(10, 0, 0);
    }
}
