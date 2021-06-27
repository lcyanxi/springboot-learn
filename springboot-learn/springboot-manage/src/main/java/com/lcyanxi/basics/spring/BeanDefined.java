package com.lcyanxi.basics.spring;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author lichang
 * @date 2020/7/13
 */
@Data
public class BeanDefined {
    /*
     *  模拟 解析 xml 标签属性值
     *   <bean id  ,class,  scope.factory-bean,factory-method>
     **/

    /**
     * beanId
     */
    private String beanId;
    /**
     * bean path
     */
    private String classPath;
    /**
     * 对象类型  默认为单列
     */
    private String scope ="singleton";
    /**
     * bean factory
     */
    private String factoryBean = null;

    /**
     * bean method
     */
    private String factoryMethod = null;

    /**
     * 属性值
     */
    private Map<String,String> propertyMap = new HashMap<String, String>(16);
}
