package com.lcyanxi.dedup.strategy;

import java.util.function.Function;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Created by linjunjie1103@gmail.com
 */
public interface ConsumeStrategy {
     boolean invoke(Function<MessageExt, Boolean> consumeCallback, MessageExt messageExt);
}

