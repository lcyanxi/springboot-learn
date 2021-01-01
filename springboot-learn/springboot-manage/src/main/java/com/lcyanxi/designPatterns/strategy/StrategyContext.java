package com.lcyanxi.designPatterns.strategy;

import com.lcyanxi.service.IStrategyService;

/**
 * @author lichang
 * @date 2021/1/1
 */
public class StrategyContext {
    private IStrategyService strategy;

    public StrategyContext(IStrategyService strategy){
        this.strategy = strategy;
    }

    public void executeDraw(int radius, int x, int y){
         strategy.draw(radius, x, y);
    }
}
