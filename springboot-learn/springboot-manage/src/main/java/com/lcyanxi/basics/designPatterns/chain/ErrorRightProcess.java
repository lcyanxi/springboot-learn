package com.lcyanxi.basics.designPatterns.chain;

/**
 * @author lichang
 * @date 2021/1/6
 */
public class ErrorRightProcess implements Process {
    @Override
    public void doProcess(String msg) {
        System.out.println(msg + "错别字处理");
    }
}
