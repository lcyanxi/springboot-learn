package com.lcyanxi.service.impl;


import com.lcyanxi.dto.UserLessonMapper;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUserLessonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;


import java.util.List;
@Slf4j
@Service
public class UserLessonServiceImpl implements IUserLessonService {

    @Autowired
    private UserLessonMapper userLessonMapper;

    @Override
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        String application = RpcContext.getContext().getAttachment("application");
        if (!application.contains("springboot-service") || CollectionUtils.isEmpty(userLessonList)) {
            return false;
        }
        log.info("insertUserLesson userLessonList:{}",userLessonList.toString());
        return userLessonMapper.insertBatch(userLessonList);
//        return true;
    }

    @Override
    public Boolean updateByUserId(Integer userId, Integer classId) {
        log.info("updateByUserId userId:{},classId:{}",userId,classId);
        return userLessonMapper.updateByUserId(userId, classId);
    }
}
