package com.lcyanxi.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lcyanxi.canal.CanalListener;
import com.lcyanxi.model.UserLesson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/8/27
 */
@Slf4j
@Component
public class CanalUserLessonConsumer {

    @CanalListener(databaseName = "ds1",tableName = "pr_user_lesson_3")
    public void consumerUser(String sourceStr, CanalEntry.EventType eventType){
        switch (eventType){
            case INSERT:
                log.info("=========ds1:正在添加文档===========userLesson:{}",sourceStr);
                break;
            case UPDATE:
                log.info("=========ds1:正在修改文档===========userLesson:{}",sourceStr);
                break;
            case DELETE:
                System.out.println("=========正在删除文档===========");
                break;
            default:
                System.out.println("=========操作类型不匹配===========");
        }
    }
}
