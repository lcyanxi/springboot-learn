package com.lcyanxi.dto;

import com.lcyanxi.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findUserByUserNamePassword(@Param("userName") String userName,@Param("password") String password);
}
