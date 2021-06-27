package com.lcyanxi.basics.designPatterns.strategy;

import com.lcyanxi.service.IStrategyService;

/**
 * @author lichang
 * @date 2021/1/1
 */
public class GreenPenImpl implements IStrategyService {
    @Override
    public void draw(int radius, int x, int y) {
        System.out.println("用绿色笔画图，radius:" + radius + ", x:" + x + ", y:" + y);
    }
}
