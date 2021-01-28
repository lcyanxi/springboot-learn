package com.lcyanxi.service;


import com.lcyanxi.model.UserLesson;

import java.util.List;

public interface IUserLessonService {
    /**
     *  批量保存数据
     * @param userLessonList 参数实体
     * @return
     */
    Boolean insertUserLesson(List<UserLesson> userLessonList);

    Boolean updateByUserId(Integer userId,Integer classId);


    void transactionExceptionRequired(Integer userId,String userName);

    void transactionExceptionRequiredException(Integer userId,String userName);

    void transactionExceptionRequiredExceptionTry(Integer userId,String userName);

    List<UserLesson> findAll();

}
