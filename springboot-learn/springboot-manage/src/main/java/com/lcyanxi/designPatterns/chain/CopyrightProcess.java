package com.lcyanxi.designPatterns.chain;

/**
 * @author lichang
 * @date 2021/1/6
 */
public class CopyrightProcess implements  Process {
    @Override
    public void doProcess(String msg) {
        System.out.println(msg + "版权处理");
    }
}
