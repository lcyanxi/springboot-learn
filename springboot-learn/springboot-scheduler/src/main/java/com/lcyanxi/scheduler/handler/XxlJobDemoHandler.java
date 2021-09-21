package com.lcyanxi.scheduler.handler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * Date: 2021/09/20/7:15 下午
 */
@Slf4j
@Component
public class XxlJobDemoHandler extends IJobHandler {
    @Override
    @XxlJob("xxlJobDemoHandler")
    public ReturnT<String> execute(String param) throws Exception {
        log.info("XxlJobDemoHandler is starter");
        return SUCCESS;
    }
}
