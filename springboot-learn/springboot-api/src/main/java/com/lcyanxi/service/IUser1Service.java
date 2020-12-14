package com.lcyanxi.service;

import com.lcyanxi.model.User1;

public interface IUser1Service {

    int insert(User1 record);

    User1 selectByPrimaryKey(Integer id);
}
