package com.lcyanxi.controller;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import com.lcyanxi.model.TeacherInfoVO;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.crypto.Data;
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

    @Resource
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



    public static void main(String[] args) throws Exception {
        List<Integer> indexList = Lists.newArrayList(1001,1003);
        System.out.println(indexList);

        String str = indexList.stream().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(str);


//        readFile();
    }



    public static void writeFile() throws Exception{
        File file = new File("/Users/koolearn/Desktop/sql.txt");
        if (file.exists()) { // 检查scores.txt是否存在
            System.out.println("File already exists");
            System.exit(1); // 如果存在则退出程序
        }
        Date date = new Date();
        String sql = "insert into pr_divide_class_letter  ( class_id, user_id,order_no, product_id,send_status,create_uid, create_username,create_time,update_username, update_uid, update_time,is_deleted ) values";

        // 如果不存在则创建一个新文件
        try (PrintWriter output = new PrintWriter(file)) {
            output.println(sql);
            for (int i = 0 ; i < 100000; i++){
                String orderNo = "99320998"+i;
                String value = "(122952,74142608,"+orderNo+",110859,0,'admin','admin',now(),'admin','admin',now(),0),";
                System.out.println(value);
                output.println(value);
            }
        }
    }


    public static void readFile() throws Exception{
        //简写如下
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("/Users/koolearn/Desktop/data.log"), "UTF-8"));
        String line="";
        String[] arrs=null;
        List<String > aa = Lists.newArrayList();
        int index = 0;
        while ((line=br.readLine())!=null) {
            arrs=line.split("TeacherInfoVO");
            if (arrs.length>1){
                aa.add(arrs[1]);
            }else {
                if (line.contains("BLACKLIST")){
                    index = index +1 ;
                }
                System.out.println("error:"+line);
            }
        }
        br.close();

        System.out.println("===============黑名单数量:" + index);
        Map<String, List<String>> collect = aa.stream().collect(Collectors.groupingBy(item -> item));
        System.out.println("===============num:" + aa.size());
        collect.forEach((k,v)->{
            String[] as = k.replace("(","").replace(")","").split(",");
            System.out.println(as[0] + "," + as[2] + ",人数" + v.size());
        });
    }
}
