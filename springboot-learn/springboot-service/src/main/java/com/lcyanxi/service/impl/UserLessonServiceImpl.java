package com.lcyanxi.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.lcyanxi.dto.UserLessonMapper;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.List;
@Slf4j
@Service(timeout = 6000)
@Component
public class UserLessonServiceImpl implements IUserLessonService {

    @Autowired
    private UserLessonMapper userLessonMapper;

    @Override
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        if (CollectionUtils.isEmpty(userLessonList)){
            return false;
        }
        log.info("insertUserLesson userLessonList:{}",userLessonList.toString());
        return userLessonMapper.insertBatch(userLessonList);
    }
}
