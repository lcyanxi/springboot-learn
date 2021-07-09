package com.lcyanxi.reptile;

import com.lcyanxi.model.JobInfo;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;


/**
 * @author lichang
 * Date: 2021/07/08/11:10 上午
 */
@Slf4j
public class JobInfoIsDataBasePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        //判断结果集中是否有jobInfo，有则保存在数据库中
        JobInfo jobInfo = resultItems.get("jobInfo");
        log.info("JobInfoIsDataBasePipeline:{}",jobInfo);
    }
}
