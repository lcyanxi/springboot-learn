package com.lcyanxi.service;

import com.lcyanxi.model.User;

public interface IUserService {

    User findUserByUserNamePassword(String userName, String password);

    int insert(User record);


    int insertException(User record);


    int insertExceptionWithCall(User record);


    User selectByPrimaryKey(Integer id);
}
