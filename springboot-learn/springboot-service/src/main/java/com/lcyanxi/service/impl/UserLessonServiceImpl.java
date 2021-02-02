package com.lcyanxi.service.impl;


import com.google.common.collect.Lists;
import com.lcyanxi.constant.Constants;
import com.lcyanxi.dto.UserLessonMapper;
import com.lcyanxi.model.User;
import com.lcyanxi.model.User1;
import com.lcyanxi.model.UserLesson;
import com.lcyanxi.service.IUser1Service;
import com.lcyanxi.service.IUserLessonService;
import com.lcyanxi.service.IUserService;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    @Autowired
    @Qualifier("businessThreadPoolExecutor")
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean insertUserLesson(List<UserLesson> userLessonList) {
        String application = RpcContext.getContext().getAttachment(Constants.APPLICATION);
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
    @Transactional(propagation = Propagation.REQUIRED)
    public void transactionExceptionRequired(Integer userId, String userName) {
        User user = new User();
        user.setPassword("123");
        user.setUserId(userId);
        user.setUserName(userName);
        userService.insert(user) ;

        User1 user1 = new User1();
        user1.setName(userName);
        user1Service.insert(user1);

//        throw  new RuntimeException();
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

        User1 user1 = new User1();
        user1.setName(userName);
        user1Service.insert(user1);

        User user = new User();
        user.setPassword("222");
        user.setUserId(userId);
        user.setUserName(userName);
        try {
            userService.insertException(user) ;
        }catch (Exception e){
            log.error("transactionExceptionRequiredExceptionTry userService exception",e);
        }
    }

    @Override
    public List<UserLesson> findAll(){
        try {
            Future<List<UserLesson>> submit = threadPoolExecutor.submit(() -> {
                printThreadPoolStatus(threadPoolExecutor);
                return userLessonMapper.findAll();});
            return submit.get();
        }catch (Exception e){
            log.error("userLessonServiceImpl findAll is error",e);
        }
        return Lists.newArrayList();
    }

    private static void printThreadPoolStatus(ThreadPoolExecutor threadPoolExecutor){
        LinkedBlockingQueue queue = (LinkedBlockingQueue) threadPoolExecutor.getQueue();
        System.out.println(
                Thread.currentThread().getName() + "_" + ":" +
                        "核心线程数:" + threadPoolExecutor.getCorePoolSize() +
                        "活动线程数:" + threadPoolExecutor.getActiveCount() +
                        "最大线程数:" + threadPoolExecutor.getMaximumPoolSize() +
                        "线程池活跃度:" + divide(threadPoolExecutor.getActiveCount(),threadPoolExecutor.getMaximumPoolSize()) +
                        "任务完成数:" + threadPoolExecutor.getCompletedTaskCount() +
                        "队列大小:" + (queue.size() + queue.remainingCapacity()) +
                        "当前线程排队线程数:" + queue.size() +
                        "队列剩余大小:" + queue.remainingCapacity() +
                        "队列使用度:" + divide(queue.size(),queue.size() + queue.remainingCapacity()));

    }
    private static String divide(int num1, int num2){
        return String.format("%1.2f%%",Double.parseDouble(num1 + "")/Double.parseDouble(num2 + "")*100);
    }
}
