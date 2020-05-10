package com.lcyanxi.controller;


import com.alibaba.fastjson.JSONObject;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private IUserLessonService userLessonService;

    @Resource
    private DefaultMQProducer defaultMQProducer;


    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "hello world";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(Integer productId,String userId){
        List<UserLesson> lessons = new ArrayList<>();
        String name = "admin";
        for (int i = 0 ;i < 10 ;i++){
            UserLesson userLesson=new UserLesson();
            userLesson.setParentClassId(1);
            userLesson.setBuyStatus(false);
            userLesson.setOrderNo(System.currentTimeMillis()+"");
            userLesson.setClassId(1);
            userLesson.setBuyTime(new Date());
            userLesson.setClassCourseId(11);
            userLesson.setLessonId(11);
            userLesson.setStatus(2);
            userLesson.setCreateUid(name);
            userLesson.setCreateUsername(name);
            userLesson.setUpdateUid(name);
            userLesson.setUpdateUsername(name);
            userLesson.setProductId(productId);
            userLesson.setUserId(Integer.parseInt(userId));
            lessons.add(userLesson);
        }

        try {
            Message sendMsg = new Message(RocketTopicInfoEnum.USER_LESSON_TOPIC.getTopic(), JSONObject.toJSONBytes(lessons));
            SendResult sendResult = defaultMQProducer.send(sendMsg);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK){
                return name+"登陆成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name+"登陆失败";
    }
}
