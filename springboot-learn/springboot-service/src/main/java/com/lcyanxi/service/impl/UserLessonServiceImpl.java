package com.lcyanxi.service.impl;


import com.google.common.collect.Lists;
import com.lcyanxi.dto.UserLessonMapper;
import com.lcyanxi.model.User;
import com.lcyanxi.model.User1;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUser1Service;
import com.lcyanxi.service.IUserLessonService;
import com.lcyanxi.service.IUserService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.List;
@Slf4j
@DubboService
public class UserLessonServiceImpl implements IUserLessonService {

    @Autowired
    private UserLessonMapper userLessonMapper;

    @Autowired
    private IUserService  userService;

    @Autowired
    private IUser1Service user1Service;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        String application = RpcContext.getContext().getAttachment("application");
        if (!application.contains("springboot-service") || CollectionUtils.isEmpty(userLessonList)) {
            return false;
        }
        log.info("insertUserLesson userLessonList:{}",userLessonList.toString());
        return userLessonMapper.insertBatch(userLessonList);
    }


    @Override
    public Boolean updateByUserId(Integer userId, Integer classId) {
        log.info("updateByUserId userId:{},classId:{}",userId,classId);
        return userLessonMapper.updateByUserId(userId, classId);
    }


    @Override
    public void transactionExceptionRequired(Integer userId, String userName) {
        User user = new User();
        user.setPassword("123");
        user.setUserId(userId);
        user.setUserName(userName);
        userService.insert(user) ;

        User1 user1 = new User1();
        user1.setName(userName);
        user1Service.insert(user1);

        throw  new RuntimeException();
    }


    @Override
    @Transactional
    public void transactionExceptionRequiredException(Integer userId, String userName) {
        User user = new User();
        user.setPassword("123");
        user.setUserId(userId);
        user.setUserName(userName);
        userService.insertException(user) ;

        User1 user1 = new User1();
        user1.setName(userName);
        user1Service.insert(user1);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void transactionExceptionRequiredExceptionTry(Integer userId, String userName) {
        User user = new User();
        user.setPassword("123");
        user.setUserId(userId);
        user.setUserName(userName);
        try {
            userService.insertException(user) ;
        }catch (Exception e){
            log.error("transactionExceptionRequiredExceptionTry userService exception",e);
        }

        User1 user1 = new User1();
        user1.setName(userName);
        user1Service.insert(user1);
    }
}
