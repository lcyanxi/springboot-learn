package com.lcyanxi.service.impl;


import com.lcyanxi.dto.UserLessonMapper;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;

@Service("userLessonService")
public class UserLessonServiceImpl implements IUserLessonService {

    @Autowired
    private UserLessonMapper userLessonMapper;

    @Override
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        if (CollectionUtils.isEmpty(userLessonList)){
            return false;
        }
        System.out.println("insertUserLesson userLessonList"+userLessonList.toString());
        return userLessonMapper.insertBatch(userLessonList);
    }
}
