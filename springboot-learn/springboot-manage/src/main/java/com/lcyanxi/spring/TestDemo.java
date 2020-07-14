package com.lcyanxi.spring;

import com.lcyanxi.model.UserLesson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lichang
 * @date 2020/7/13
 */
public class TestDemo {

    public static void main(String[] args) throws Exception {
        //1.声明注册bean


        BeanDefined beanObj = new BeanDefined();
        beanObj.setBeanId("userLesson");
        beanObj.setClassPath("com.lcyanxi.model.UserLesson");
        /*
         *  <property>
         *
         **/
        Map<String, String> propertyMap =  beanObj.getPropertyMap();
        propertyMap.put("userId", "202007013");
        propertyMap.put("orderNo", "1111111");


        List configuration = new ArrayList();
        configuration.add(beanObj);//spring核心配置


        //2.声明一个Spring提供BeanFacotory
        BeanFactory factory = new BeanFactory(configuration);


        //3.开发人员向BeanFactory索要实例对象.
        UserLesson t = (UserLesson) factory.getBean("userLesson");
        System.out.println("t =" + t);
        System.out.println(t.getUserId());
    }

}
