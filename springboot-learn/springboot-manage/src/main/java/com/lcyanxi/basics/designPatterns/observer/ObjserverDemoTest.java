package com.lcyanxi.basics.designPatterns.observer;

/**
 * @author lichang
 * @date 2021/1/1
 */
public class ObjserverDemoTest {

    public static void main(String[] args) {
        // 先定义一个主题
        Subject subject1 = new Subject();
        // 定义观察者
        new BinaryObserver(subject1);
        new HexaObserver(subject1);
        // 模拟数据变更，这个时候，观察者们的 update 方法将会被调用
        subject1.setState(11);
    }
}
