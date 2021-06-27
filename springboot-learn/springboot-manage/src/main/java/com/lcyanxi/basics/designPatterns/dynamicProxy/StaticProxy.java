package com.lcyanxi.basics.designPatterns.dynamicProxy;

import com.lcyanxi.service.ICountService;
import com.lcyanxi.serviceImpl.CountServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * 静态代理
 * @author lichang
 * @date 2020/12/5
 */
@Slf4j
public class StaticProxy implements ICountService {
    //代理类持有一个委托类的对象引用
    private ICountService delegate;

    public StaticProxy(ICountService delegate) {
        this.delegate = delegate;
    }

    @Override
    public int count() {
        long startTime = System.currentTimeMillis();
        int count = delegate.count();
        long entTime = System.currentTimeMillis();
        log.info("StaticProxy execute time:[{}],count:[{}] ",(entTime - startTime),count);
        return count;
    }

    public static void main(String[] args) {
        StaticProxy staticProxy = new StaticProxy(new CountServiceImpl());
        for (int i = 0; i < 100; i++){
            staticProxy.count();
        }
    }
}
