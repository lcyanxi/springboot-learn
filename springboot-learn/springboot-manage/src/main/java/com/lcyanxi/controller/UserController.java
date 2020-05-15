package com.lcyanxi.controller;


import com.alibaba.fastjson.JSONObject;
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
import java.util.Set;
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



    public static void main(String[] args) throws Exception {
        readFile();
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
            // 若没有try-with-resources结构则必须使用 close() 关闭文件，否则数据就不能正常地保存在文件中
            // output.close();
        }
    }


    public static void readFile() throws Exception{
        //简写如下
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("/Users/koolearn/Desktop/teacherinfo.log"), "UTF-8"));
        String line="";
        String[] arrs=null;
        Set<String > aa = Sets.newHashSet();
        while ((line=br.readLine())!=null) {
            arrs=line.split("TeacherInfoVO");
            if (arrs.length>1){
                aa.add(arrs[1]);
            }else {
                System.out.println("error:"+line);
            }
        }
        br.close();

        for (String bb : aa){
            String[] as = bb.replace("(","").replace(")","").split(",");
            System.out.println(as[0]+","+as[2]);
        }
    }
}
