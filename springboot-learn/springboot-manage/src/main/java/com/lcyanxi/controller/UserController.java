package com.lcyanxi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import com.lcyanxi.jwt.JWTUtils;
import com.lcyanxi.model.User;
import com.lcyanxi.model.UserDemo;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import com.lcyanxi.service.IUserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    @RequestMapping(value = "/user/index",method = RequestMethod.GET)
    public String index(){
        return "hello world";
    }

    @GetMapping("/login")
    public Map<String, Object> login(User user) {
        log.info("用户名：{}", user.getUserName());
        log.info("password: {}", user.getPassword());

        Map<String, Object> map = new HashMap<>();

        try {
            User userDB = userService.findUserByUserNamePassword(user.getUserName(),user.getPassword());

            Map<String, String> payload = new HashMap<>();
            payload.put("userName", userDB.getUserName());
            payload.put("password", userDB.getPassword());
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
    public String findAllData(){
       log.info("findAllData times:[{}]",System.currentTimeMillis());
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


    @PostMapping(value = "/test-post",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String process2Post(@RequestBody UserDemo demo) throws IOException {
        log.info("process2Post param :[{}]"+demo);
        return JSON.toJSONString(demo);
    }

    @GetMapping(value = "/test-get/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String process(@PathVariable Integer userId, String userName){
        UserDemo userDemo = UserDemo.builder().userId(userId).age(11).userName(userName).build();
        log.info("process is result:[{}]" + userDemo);
        return JSON.toJSONString(userDemo);
    }

    @DeleteMapping(value = "/deleted/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteBodyDimension(@PathVariable String userId) {
        return "SUCCESS";
    }

    public  static void readFileData(String path) throws Exception{
        //简写如下
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String line ;
        String[] arrs ;
        Set<String> orders = Sets.newHashSet();
        Set<Integer> products = Sets.newHashSet();
        while ((line = br.readLine()) != null) {
            arrs=line.split(",");
            String orderNo = arrs[0];
            Integer productId = Integer.parseInt(arrs[1]);
            orders.add(orderNo);
            products.add(productId);
        }

        System.out.println("select order_no,product_id,status from pr_user_class where is_deleted = 0 and  order_no in("+
                orders.stream().map(String::valueOf).collect(Collectors.joining(","))+") and product_id in ("+
                products.stream().map(String::valueOf).collect(Collectors.joining(","))+")");


        System.out.println("select order_no,product_id,status from tb_user_product where is_deleted = 0 and  order_no in("+
                orders.stream().map(String::valueOf).collect(Collectors.joining(","))+") and product_id in ("+
                products.stream().map(String::valueOf).collect(Collectors.joining(","))+")");
        br.close();
    }


    public  boolean isNumber(Object o){
        return  (Pattern.compile("[0-9]*")).matcher(String.valueOf(o)).matches();
    }


    @Data
    class Info {
        private Integer id;
        private String orderNo ;
        private Integer productId;
    }

}
