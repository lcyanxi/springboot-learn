package com.lcyanxi.springbootservice;


import com.lcyanxi.model.User;
import com.lcyanxi.service.IUser1Service;
import com.lcyanxi.service.IUserLessonService;
import com.lcyanxi.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootServiceApplicationTests {


    @Autowired
    DataSource dataSource;

    @Autowired
    private IUserLessonService userLessonService;

    @Autowired
    private IUserService userService;

    @Test
    public void test() throws Exception{
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();
    }

    @Test
    public void transactionExceptionRequiredTest(){
        userLessonService.transactionExceptionRequired(123124,"zhangsan");
        System.out.println();

    }

    @Test
    public void transactionExceptionRequiredExceptionTryTest(){
        userLessonService.transactionExceptionRequiredExceptionTry(12111,"lisi");
        System.out.println();
    }

    @Test
    public void insertExceptionWithCallTest(){
        User user = new User();
        user.setUserName("wangwu");
        user.setUserId(1211);
        user.setPassword("121212");
        userService.insertExceptionWithCall(user);
        System.out.println();
    }


}
