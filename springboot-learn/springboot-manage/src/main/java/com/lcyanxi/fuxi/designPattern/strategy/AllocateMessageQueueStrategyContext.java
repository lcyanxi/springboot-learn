package com.lcyanxi.fuxi.designPattern.strategy;

/**
 * 策略模式
 *
 * > 策略模式[1-5]定义了一系列的算法，并将每一个算法封装起来，使它们可以相互替换。策略模式通常包含以下角色：
 *
 * > **抽象策略（Strategy）类**：定义了一个公共接口，各种不同的算法以不同的方式实现这个接口，环境角色使用这个接口调用不同的算法，一般使用接口或抽象类实现。
 * **具体策略（Concrete Strategy）类**：实现了抽象策略定义的接口，提供具体的算法实现。
 * **环境（Context）类**：持有一个策略类的引用，最终给客户端调用
 */
public class AllocateMessageQueueStrategyContext {

    public static AllocateMessageQueueStrategy getStrategy(String strategyName){
        switch (strategyName){
            case "平均分配":
                return new AllocateMessageQueueAveragely();
            case "平均轮询分配":
                return new AllocateMessageQueueAveragelyByCircle();
            case "一致性 hash":
                return new AllocateMessageQueueConsistentHash();
            case "同机房分配":
                return new AllocateMessageQueueByMachineRoom();
            default:
                return new AllocateMessageQueueAveragely();
        }
    }

    public static void main(String[] args) {
        AllocateMessageQueueStrategy strategy = getStrategy("同机房分配");
        String name = strategy.getName();
        System.out.println(name);
    }

}
