package com.lcyanxi.controller;


import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private IUserLessonService userLessonService;


    @RequestMapping(value = "/index",method = RequestMethod.GET)
    @ResponseBody
    public String index(){
        return "hello world";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    public String login(String name){
        List<UserLesson> lessons = new ArrayList<>();
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
        userLesson.setProductId(11);
        userLesson.setUserId(Integer.parseInt(name));
        lessons.add(userLesson);
        System.out.println("userLessonService"+userLessonService);
        boolean result = userLessonService.insertUserLesson(lessons);
        return name+"登陆"+result;
    }
}
