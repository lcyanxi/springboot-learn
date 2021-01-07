package com.lcyanxi.designPatterns.chain;

/**
 * @author lichang
 * @date 2021/1/6
 */
public class SensitiveWordProcess implements Process {
    @Override
    public void doProcess(String msg) {
        System.out.println(msg + "敏感词处理");
    }
}
