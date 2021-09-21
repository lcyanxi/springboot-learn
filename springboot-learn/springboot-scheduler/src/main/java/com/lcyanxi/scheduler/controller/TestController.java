package com.lcyanxi.scheduler.controller;

import com.lcyanxi.scheduler.handler.XxlJobDemoHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lichang
 * Date: 2021/09/21/12:15 上午
 */
@RestController
public class TestController {
    @Resource
    private XxlJobDemoHandler xxlJobDemoHandler;

    @GetMapping("/xxlJob")
    public String xxlJobDemoTest() throws Exception{
        return xxlJobDemoHandler.execute("").toString();
    }
}
