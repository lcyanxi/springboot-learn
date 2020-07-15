package com.lcyanxi.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lcyanxi.enums.RocketTopicInfoEnum;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.dubbo.config.annotation.Reference;
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

    @Reference
    private IUserLessonService userLessonService;

    @Resource
    private DefaultMQProducer defaultMQProducer;


    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "hello world";
    }


    @RequestMapping(value = "/addUserLesson",method = RequestMethod.GET)
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


//    @RequestMapping(value = "/orderly",method = RequestMethod.GET)
//    public String orderly(String orderNo){
//        try {
//            Map<Object,Object> map = Maps.newHashMap();
//            map.put("orderNo",orderNo);
//            map.put("ts",System.currentTimeMillis());
//            Message sendMsg = new Message(RocketTopicInfoEnum.ORDERLY_TOPIC.getTopic(), JSONObject.toJSONBytes(map));
//            SendResult sendResult = defaultMQProducer.send(sendMsg);
//            if (sendResult.getSendStatus() == SendStatus.SEND_OK){
//                return  "下单成功";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "下单失败";
//    }



    public static void main(String[] args) throws Exception {
//        List<Integer> indexList = Lists.newArrayList(1001,1003);
//        String str = indexList.stream().map(String::valueOf).collect(Collectors.joining(","));
//        System.out.println(str);
        UserController userController = new UserController();
        String logPath = "/Users/koolearn/Desktop/data.csv";
        readFileData(logPath);

//        List<Info> targetDataList = userController.readDataUtil(logPath,false);
//        System.out.println("num:"+targetDataList.size());
//        String aNumberPath = "/Users/koolearn/Desktop/a.csv";
//        String bPath = "/Users/koolearn/Desktop/b.csv";
//
//        List<Info> aDataList = userController.readDataUtil(aNumberPath,true);
//        List<Info> bDataList = userController.readDataUtil(bPath,true);
//        aDataList.addAll(bDataList);
//
//        List<Integer> ids = Lists.newArrayList();
//
//        targetDataList.forEach((info -> {
//            for (Info aInfo : aDataList){
//                if (aInfo.getProductId().equals(info.getProductId()) && info.getOrderNo().equals(aInfo.orderNo)){
//                    ids.add(aInfo.getId());
//                    break;
//                }
//            }
//        }));
//        System.out.println("bNum:"+ids.size());
//        System.out.println(ids.stream().map(String::valueOf).collect(Collectors.joining(",")));



//        readFile();
//        writeFile();
    }



    public static void writeFile() throws Exception{
        File file = new File("/Users/koolearn/Desktop/sql.txt");
        if (file.exists()) { // 检查scores.txt是否存在
            System.out.println("File already exists");
            System.exit(1); // 如果存在则退出程序
        }
        Date date = new Date();
        String sql = "insert into pr_user_class  ( class_id, parent_class_id, user_id,order_no, product_id,re_buy,join_status,status,create_uid, create_username,create_time,update_username, update_uid, update_time,is_deleted ) values";

        List<Integer> subClassIds = Lists.newArrayList(130174,130175,130176);
        List<Integer> userIds = Lists.newArrayList(73782521,73721148,74146775,73670601,74146774,74146773,74146769,73721150,73652233);

        Random r = new Random(1);
        // 如果不存在则创建一个新文件
        try (PrintWriter output = new PrintWriter(file)) {
            output.println(sql);
            for (Integer subClassId : subClassIds){
                for (int i = 0 ; i < 599; i++){
                    int ran1 = r.nextInt(userIds.size());
                    String orderNo = "99320998"+i;
                    String value = "("+subClassId+",130173,"+userIds.get(ran1)+","+orderNo+",112572,0,1,1,'admin','admin',now(),'admin','admin',now(),0),";
                    System.out.println(value);
                    output.println(value);
                }
            }
        }
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

    public   Map<Boolean,List<String>> readFileLog(String path) throws Exception{
        //简写如下
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String line ;
        String[] arrs ;
        List<Info> infos = Lists.newArrayList();
        while ((line=br.readLine())!=null) {
            arrs=line.split(",");
            String orderNo = arrs[0];
            Integer productId = Integer.parseInt(arrs[1]);
            Info info = new Info();
            info.setOrderNo(orderNo);
            info.setProductId(productId);
            infos.add(info);
        }
        br.close();



        File file = new File("/Users/koolearn/Desktop/sql-bak.txt");
        List<String> orders = Lists.newArrayList();
        // 如果不存在则创建一个新文件
        try (PrintWriter output = new PrintWriter(file)) {
            Map<Integer, List<Info>> collect = infos.stream().collect(Collectors.groupingBy(Info::getProductId));
            collect.forEach((k,v)->{
                orders.addAll(v.stream().map(Info::getOrderNo).collect(Collectors.toList()));
                String orderNos = v.stream().map(Info::getOrderNo).collect(Collectors.joining(","));
                String sql = "update pr_divide_class_letter set send_status = 1  where send_status = 0  and  product_id = " + k + " and order_no in (" + orderNos + ");";
                output.println(sql);
            });
        }
       return orders.stream().collect(Collectors.groupingBy(this::isNumber));
//        tempMap.get(false).forEach((order->{
//            String temp = "\'"+order+"\',";
//            System.out.print(temp);
//        }));
//        System.out.println();
//        System.out.println(String.join(",", tempMap.get(true)));


    }

    public  boolean isNumber(Object o){
        return  (Pattern.compile("[0-9]*")).matcher(String.valueOf(o)).matches();
    }

    private  List<Info> readDataUtil(String path,boolean type) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String line ;
        String[] arrs ;
        List<Info> infos = Lists.newArrayList();
        while ((line = br.readLine()) != null) {
            arrs=line.split(",");
            Info info = new Info();

            if (type){
                Integer id = Integer.parseInt(arrs[0]);
                Integer productId = Integer.parseInt(arrs[1]);
                String orderNo = arrs[2];
                info.setId(id);
                info.setOrderNo(orderNo);
                info.setProductId(productId);
            }else {
                String orderNo = arrs[0];
                Integer productId = Integer.parseInt(arrs[1]);
                info.setOrderNo(orderNo);
                info.setProductId(productId);
            }
            infos.add(info);
        }
        br.close();
        return infos;
    }

    @Data
    class Info {
        private Integer id;
        private String orderNo ;
        private Integer productId;
    }


    public static void readFile() throws Exception{
        //简写如下
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("/Users/koolearn/Desktop/provider.log"), "UTF-8"));
        String line="";
        String[] arrs=null;
        List<String > aa = Lists.newArrayList();
        int index = 0;
        Set<String> rootTeacherIds = Sets.newHashSet();
        while ((line=br.readLine())!=null) {
            if (!line.contains("divideClassLetterHandle")){
                continue;
            }
            arrs=line.split("TeacherInfoVO");
            if (arrs.length>1){
                if (line.contains("暖心伴学")){
                    rootTeacherIds.add(line.split(",")[2]);
                }else {
                    aa.add(arrs[1]);
                }
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
        System.out.println("===============:" + aa.size());
        collect.forEach((k,v)->{
                String[] as = k.replace("(","").replace(")","").split(",");
                System.out.println(as[0] + "," + as[2] + ",人数" + v.size());
        });

        System.out.println("虚拟学管 = "+rootTeacherIds.size());
        for(String  subClassId : rootTeacherIds){
            System.out.print(subClassId.split(":")[1]+",");
        }
    }
}
