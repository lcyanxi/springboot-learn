package com.lcyanxi.service.impl;

import com.lcyanxi.dto.UserMapper;
import com.lcyanxi.model.User;
import com.lcyanxi.service.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


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


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int insert(User record) {
        return userMapper.insert(record);
    }

    @Override
    public User selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int insertException(User record) {
        userMapper.insert(record);
        throw new RuntimeException();
    }
}
