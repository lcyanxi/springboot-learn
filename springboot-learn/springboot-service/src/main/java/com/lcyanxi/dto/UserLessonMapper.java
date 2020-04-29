package com.lcyanxi.dto;

import com.lcyanxi.model.UserLesson;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserLessonMapper {

    boolean  insertBatch(@Param("userLessons") List<UserLesson> userLessons);
}
