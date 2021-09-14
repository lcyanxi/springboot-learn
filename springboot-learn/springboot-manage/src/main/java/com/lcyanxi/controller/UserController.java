package com.lcyanxi.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.lcyanxi.annotation.AccessControl;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import com.lcyanxi.model.User;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import com.lcyanxi.service.IUserService;
import com.lcyanxi.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.lcyanxi.content.Contents.ATTRIBUTE_CURRENT_UID;

@Slf4j
@RestController
public class UserController {

    @DubboReference
    private IUserLessonService userLessonService;

    @DubboReference
    private IUserService userService;

    @Resource
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Value("${date.value}")
    private String dataValue;


    @RequestMapping(value = "/user/index",method = RequestMethod.GET)
    public String index(){
        return "hello world";
    }

    @GetMapping("/login")
    public Map<String, Object> login(String userName, String password) {
        log.info("login userName：{}, password: {},dataValue:{}", userName,password,dataValue);
        Map<String, Object> map = new HashMap<>();

        try {
            User userDB = userService.findUserByUserNamePassword(userName,password);
            Map<String, String> payload = new HashMap<>();
            payload.put("userName", userDB.getUserName());
            payload.put("password", userDB.getPassword());
            payload.put(ATTRIBUTE_CURRENT_UID,userName+password);
            String token = JWTUtils.getToken(payload);

            map.put("state", true);
            map.put("msg", "登录成功");
            map.put("token", token);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state", false);
            map.put("msg", e.getMessage());
            map.put("token", "");
        }
        return map;
    }

    @GetMapping("/find")
    @AccessControl
    public String findAllData(@RequestAttribute(value = ATTRIBUTE_CURRENT_UID) String userId){
       log.info("findAllData userId:[{}] , times:[{}]",userId,System.currentTimeMillis());
        return "获得" + userLessonService.findAll().size() +"条数据";
    }


    @RequestMapping(value = "/user/addUserLesson",method = RequestMethod.GET)
    public String addUserLesson(Integer productId,String userId){
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
        Boolean result = userLessonService.insertUserLesson(lessons);
        return "添加课次结果:"+result;
    }

    @RequestMapping(value = "/update",method = RequestMethod.GET)
    public String update(Integer userId,Integer classId){
        Boolean result = userLessonService.updateByUserId(userId, classId);
        String message = result ? "成功" : "失败";
        return   "登陆 :" + message;
    }

    @RequestMapping(value = "/orderly",method = RequestMethod.GET)
    public String orderly(String orderNo){
        try {
            Map<Object,Object> map = Maps.newHashMap();
            map.put("orderNo",orderNo);
            map.put("ts",System.currentTimeMillis());
            Message sendMsg = new Message(RocketTopicInfoEnum.ORDERLY_TOPIC.getTopic(), JSONObject.toJSONBytes(map));
            SendResult sendResult = defaultMQProducer.send(sendMsg);
            if (sendResult.getSendStatus() == SendStatus.SEND_OK){
                return  "下单成功";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "下单失败";
    }

    @GetMapping(value = "/thread-pool-info")
    public String threadPoolTest(){
        return "coreSize: " + threadPoolExecutor.getCorePoolSize() + ", maxSize: " + threadPoolExecutor.getMaximumPoolSize() + ", poolSize: " + threadPoolExecutor.getPoolSize();
    }


    @DeleteMapping(value = "/deleted/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteBodyDimension(@PathVariable String userId) {
        return "SUCCESS";
    }

}
