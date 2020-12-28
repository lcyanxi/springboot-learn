package com.lcyanxi.consumer;

import com.alibaba.fastjson.JSON;
import com.lcyanxi.dedup.DedupConcurrentListener;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * @author lichang
 * @date 2020/12/28
 */
@Slf4j
@Component
public class DedupDemoConsumer extends DedupConcurrentListener {

    @Override
    protected boolean doHandleMsg(MessageExt messageExt) {
        if (Objects.nonNull(messageExt)){
            log.info("topic:[{}] 接受到的消息为:[{}]",messageExt.getTopic(),JSON.toJSONString(messageExt));
        }
        return true;
    }
}
