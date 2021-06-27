package com.lcyanxi.basics.designPatterns.chain;

/**
 * 责任链模式
 * @author lichang
 * @date 2021/1/6
 */
public class ChainDemo {
    public static void main(String[] args) {
        String msg = "内容内容内容==" ;
        MsgProcessChain chain = new MsgProcessChain()
                .addChain(new SensitiveWordProcess())
                .addChain(new ErrorRightProcess())
                .addChain(new CopyrightProcess()) ;
        chain.process(msg) ;
    }
}
