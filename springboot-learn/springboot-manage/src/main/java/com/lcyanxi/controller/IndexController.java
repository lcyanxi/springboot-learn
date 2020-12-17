package com.lcyanxi.controller;

import com.lcyanxi.annotation.MyAutowired;

import com.lcyanxi.service.MyAutowiredService;

/**
 * @author lichang
 * @date 2020/12/16
 */
public class IndexController {

    @MyAutowired
    private MyAutowiredService myAutowiredService;

    public MyAutowiredService getCountServiceIntense(){
        return myAutowiredService;
    }
}
