package com.lcyanxi.dedup.strategy;


import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Created by linjunjie1103@gmail.com
 */
@Slf4j
@AllArgsConstructor
public class NormalConsumeStrategy implements ConsumeStrategy {

    @Override
    public boolean invoke(Function<MessageExt, Boolean> consumeCallback, MessageExt messageExt) {
        return consumeCallback.apply(messageExt);
    }
}