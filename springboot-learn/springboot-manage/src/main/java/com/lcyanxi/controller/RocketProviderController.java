package com.lcyanxi.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author lichang
 * @date 2020/12/28
 */
@Controller
public class RocketProviderController {

    @Resource
    private DefaultMQProducer defaultMQProducer;

    @RequestMapping(value = "/send/msg",method = RequestMethod.GET)
    public String sendDedupMsg(String userName){

        String msg = "send msg result:";

        Map<String,String> map = Maps.newHashMap();
        map.put("userName",userName);

        Message message = new Message(RocketTopicInfoEnum.SEND_DEDUP_TOPIC.getTopic(), JSONObject.toJSONBytes(map));
        try {
            SendResult send = defaultMQProducer.send(message);
            return msg + send.getSendStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg + false;
    }


}
