package com.lcyanxi.fuxi.designPattern.chain;

public class HandlerInterceptorDemo {

    public static void main(String[] args) {
        MyHandlerExecutionChain chain = new MyHandlerExecutionChain(new StartHandlerInterceptor());
        chain.addInterceptor(new BindCheckHandlerInterceptor());
        chain.addInterceptor(new LogHandlerInterceptor());

    }
}
