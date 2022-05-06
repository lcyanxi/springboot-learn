package com.lcyanxi.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lcyanxi.rocketmq.base.Result;
import com.lcyanxi.config.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

import static com.lcyanxi.topic.RocketTopic.SEND_DEDUP_TOPIC;
import static com.lcyanxi.topic.RocketTopic.USER_LESSON_TOPIC;

/**
 * @author lichang
 * @date 2020/12/28
 */
@Slf4j
@RestController
public class MQController {

    @Resource
    private MQProducer mqProducer;

    @RequestMapping(value = "/send/msg",method = RequestMethod.GET)
    public String sendDedupMsg(String userName){
        log.info("sendDedupMsg userName:[{}]",userName);
        String msg = "send msg result:";

        Map<String,String> map = Maps.newHashMap();
        map.put("userName",userName);
        Message message = new Message(USER_LESSON_TOPIC, JSONObject.toJSONBytes(JSONObject.toJSONString(map)));
        Result<SendResult> sendResultResult = mqProducer.syncSendOrderly(message, userName);
        return msg + sendResultResult.isSuccess;
    }


    @RequestMapping(value = "/sendMq",method = RequestMethod.GET)
    public String login(Integer productId,String userId){
        String name = "kangkang";
        Map<String,Object> map = Maps.newHashMap();
        map.put("productId",productId);
        map.put("userId",userId);
        map.put("userName",name);
        Message sendMsg = new Message(SEND_DEDUP_TOPIC, JSONObject.toJSONBytes(JSONObject.toJSONString(map)));

        mqProducer.asyncSend(sendMsg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("asyncSend onSuccess");
            }

            @Override
            public void onException(Throwable e) {
                log.error("asyncSend onException :",e);
            }
        });
        return name + "登陆成功";
    }

}
