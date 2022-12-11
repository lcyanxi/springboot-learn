package com.lcyanxi.manage;

import com.lcyanxi.service.ICountService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author lichang
 * @date 2020/12/16
 */
public class MyTest {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.yaml");

    @Test
    public void test(){
        ICountService dataSource = context.getBean("dataSource", ICountService.class);
    }
}
