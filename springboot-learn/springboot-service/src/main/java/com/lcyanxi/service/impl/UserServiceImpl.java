package com.lcyanxi.service.impl;

import com.lcyanxi.dto.UserMapper;
import com.lcyanxi.model.User;
import com.lcyanxi.service.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author lichang
 * @date 2020/11/28
 */
@DubboService
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserByUserNamePassword(String userName, String password) {
        return userMapper.findUserByUserNamePassword(userName,password);
    }
}
