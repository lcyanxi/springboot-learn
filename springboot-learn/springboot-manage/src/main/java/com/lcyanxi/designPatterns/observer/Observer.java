package com.lcyanxi.designPatterns.observer;

/**
 * @author lichang
 * @date 2021/1/1
 */
public abstract class Observer {
    protected Subject subject;
    public abstract void update();
}
