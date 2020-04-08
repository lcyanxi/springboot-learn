package com.lcyanxi.service.impl;


import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import org.springframework.stereotype.Service;


import java.util.List;

@Service("userLessonService")
public class UserLessonServiceImpl implements IUserLessonService {

    @Override
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        System.out.println("insertUserLesson userLessonList"+userLessonList.toString());
        return true;
    }
}
