package com.lcyanxi.controller;

import com.lcyanxi.annotation.MyAutowired;

import com.lcyanxi.service.IMyAutowiredService;
import com.lcyanxi.service.MyAutowiredService;

/**
 * @author lichang
 * @date 2020/12/16
 */
public class IndexController {

//    @MyAutowired
//    private MyAutowiredService myAutowiredService;
    @MyAutowired
    private IMyAutowiredService myAutowiredService;

    public IMyAutowiredService getCountServiceIntense(){
        return myAutowiredService;
    }
}
