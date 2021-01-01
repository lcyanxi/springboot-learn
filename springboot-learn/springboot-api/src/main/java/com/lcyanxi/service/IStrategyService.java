package com.lcyanxi.service;

/**
 * 策略模式：模拟场景画图可以用什么类型的笔来画
 */
public interface IStrategyService {

    void draw(int radius, int x, int y);
}
