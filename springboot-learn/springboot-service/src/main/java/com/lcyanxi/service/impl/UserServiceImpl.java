package com.lcyanxi.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.lcyanxi.dto.UserMapper;
import com.lcyanxi.model.User;
import com.lcyanxi.redis.annotation.RedissonLock;
import com.lcyanxi.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


/**
 * @author lichang
 * @date 2020/11/28
 */
@DubboService
@Slf4j
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Value("${date.value}")
    private String dataValue;

    @Override
    @Cached(name = "findUserByUserNamePassword", key = "#userName", expire = 1, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    @RedissonLock(lockPre = "findUserByUserNamePassword",value = "#userName",leaseSeconds = 3)
    public User findUserByUserNamePassword(String userName, String password) {
        log.info("findUserByUserNamePassword userName:{},password:{},dataValue:{}", userName, password, dataValue);
        return userMapper.findUserByUserNamePassword(userName, password);
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

    @Override
    public int insertExceptionWithCall(User record) {
        return insertException(record);
    }
}
