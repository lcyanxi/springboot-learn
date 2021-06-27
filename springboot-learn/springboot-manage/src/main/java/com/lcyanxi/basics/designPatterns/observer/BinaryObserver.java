package com.lcyanxi.basics.designPatterns.observer;

/**
 * @author lichang
 * @date 2021/1/1
 */
public class BinaryObserver extends Observer {

    // 在构造方法中进行订阅主题
    public BinaryObserver(Subject subject) {
        this.subject = subject;
        // 通常在构造方法中将 this 发布出去的操作一定要小心
        this.subject.attach(this);
    }

    /**
     * 该方法由主题类在数据变更的时候进行调用
     */
    @Override
    public void update() {
        String result = Integer.toBinaryString(subject.getState());
        System.out.println("订阅的数据发生变化，新的数据处理为二进制值为：" + result);
    }
}
