package com.lcyanxi.reptile;

import com.alibaba.fastjson.JSON;
import com.lcyanxi.model.LianInfo;
import com.lcyanxi.model.LianJiaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.FileWriter;
import java.util.List;
import java.util.Objects;

/**
 * @author lichang
 * Date: 2021/07/08/4:23 下午
 */
@Slf4j
public class LianJiaDataPipeline implements Pipeline {
    private static final String fileNameDetail = "/Users/lichang/Desktop/lianJiaDetailData.text";
    private static final String fileNameInfo = "/Users/lichang/Desktop/lianData.text";

    @Override
    public void process(ResultItems resultItems, Task task) {
        //判断结果集中是否有jobInfo，有则保存在数据库中
        LianJiaInfo info = resultItems.get("info");
        List<LianInfo> lianInfos = resultItems.get("lianInfo");
        if (CollectionUtils.isNotEmpty(lianInfos)){
            sendToFile(JSON.toJSONString(lianInfos),fileNameInfo);
        }
        if (Objects.nonNull(info)){
            sendToFile(info.toString(),fileNameDetail);
        }
    }

    private void sendToFile(String data,String fileName){
        try (FileWriter fileWriter = new FileWriter(fileName,true)){
            fileWriter.append(data);
        }catch (Exception e){
            log.error("LianJiaDataPipeline is exception",e);
        }
    }
}
