package com.lcyanxi.service.impl;

import com.lcyanxi.dto.User1Mapper;
import com.lcyanxi.model.User1;
import com.lcyanxi.service.IUser1Service;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lichang
 * @date 2020/12/14
 */
@DubboService
public class User1ServiceImpl implements IUser1Service {

    @Autowired
    private User1Mapper user1Mapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int insert(User1 record) {
        return user1Mapper.insert(record);
    }

    @Override
    public User1 selectByPrimaryKey(Integer id) {
        return user1Mapper.selectByPrimaryKey(id);
    }
}
