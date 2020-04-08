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
}
