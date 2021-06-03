package com.lcyanxi.controller;

import com.lcyanxi.annotation.MyAutowired;

import com.lcyanxi.service.IMyAutowiredService;
import com.lcyanxi.service.MyAutowiredService;

import javax.annotation.Resource;

/**
 * @author lichang
 * @date 2020/12/16
 */
public class IndexController {

//    @MyAutowired
//    private MyAutowiredService myAutowiredService;
    @Resource
    private IMyAutowiredService myAutowiredService;

    public IMyAutowiredService getCountServiceIntense(){
        return myAutowiredService;
    }
}
