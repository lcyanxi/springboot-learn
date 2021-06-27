package com.lcyanxi.basics.designPatterns.observer;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author lichang
 * @date 2021/1/1
 */
@Data
public class Subject {

    private List<Observer> observers = new ArrayList<Observer>();

    private int state;

    public void setState(int state) {
        this.state = state;
        // 数据已变更，通知观察者们
        observers.forEach((Observer::update));
    }

    // 将观察者添加到主题列表
    public void attach(Observer observer){
        observers.add(observer);
    }
}
