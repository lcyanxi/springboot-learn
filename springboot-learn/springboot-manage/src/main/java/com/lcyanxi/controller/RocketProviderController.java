package com.lcyanxi.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lcyanxi.enums.RocketTopicInfoEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/12/28
 */
@Slf4j
@RestController
public class RocketProviderController {

    @Resource
    private DefaultMQProducer defaultMQProducer;

    @RequestMapping(value = "/send/msg",method = RequestMethod.GET)
    public String sendDedupMsg(String userName){
        log.info("sendDedupMsg userName:[{}]",userName);
        String msg = "send msg result:";

        Map<String,String> map = Maps.newHashMap();
        map.put("userName",userName);

        Message message = new Message(RocketTopicInfoEnum.SEND_DEDUP_TOPIC.getTopic(), JSONObject.toJSONBytes(map));
        try {
            SendResult send = defaultMQProducer.send(message);
            log.info("sendDedupMsg sendResult:[{}]",send);
            return msg + send.getSendStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg + false;
    }


    @RequestMapping(value = "/sendMq",method = RequestMethod.GET)
    public String login(Integer productId,String userId){
        String name = "kangkang";
        Map<String,Object> map = Maps.newHashMap();
        map.put("productId",productId);
        map.put("userId",userId);
        map.put("userName",name);

        try {
            Message sendMsg = new Message(RocketTopicInfoEnum.USER_LESSON_TOPIC.getTopic(), JSONObject.toJSONBytes(map));
            SendResult sendResult = defaultMQProducer.send(sendMsg);
            log.info("login send is done data:{},sendResult:{}",map,sendResult);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK){
                return name + "登陆成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name + "登陆失败";
    }

}
