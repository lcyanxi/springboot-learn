package com.lcyanxi.basics.designPatterns.chain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lichang
 * @date 2021/1/6
 */
public class MsgProcessChain {

    private List<Process> chains = new ArrayList<>() ;
    /**
     * 添加责任链
     * @param process
     * @return
     */
    public MsgProcessChain addChain(Process process){
        chains.add(process) ;
        return this ;
    }
    /**
     * 执行处理
     * @param msg
     */
    public void process(String msg){
        chains.forEach((process -> process.doProcess(msg)));
    }
}
