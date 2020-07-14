package com.lcyanxi.spring;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * 模拟spring beanFactory
 *
 * @author lichang
 * @date 2020/7/13
 */
@Data
public class BeanFactory {

    /**
     * bean 定义对象 相当于解析xml获取得到
     */
    private List<BeanDefined> beanDefinedList;
    /**
     * 已经创建好实例对象
     */
    private Map<String, Object> SpringIoc;
    /**
     * 后置对象
     */
    private BeanPostProcessor processorObj;


    public BeanFactory(List<BeanDefined> beanDefinedList) throws Exception {
        this.beanDefinedList = beanDefinedList;
        SpringIoc = new HashMap<String, Object>(16); //所有scope="singleton" 采用单类模式管理bean对象
        for (BeanDefined beanObj : this.beanDefinedList) {
            if ("singleton".equals(beanObj.getScope())) {
                Class classFile = Class.forName(beanObj.getClassPath());
                Object instance = classFile.newInstance();
                //判断当前对象是一个bean对象还是后置处理处理对象
                isProcessor(instance, classFile);
                SpringIoc.put(beanObj.getBeanId(), instance);
            }
        }

    }

    /**
     * 判断对象是否后置处理器
     *
     * @param instance  对象
     * @param classFile 对象文件
     */
    private void isProcessor(Object instance, Class classFile) {
        Class interfaceArray[] = classFile.getInterfaces();
        if (interfaceArray == null) {
            return;
        }

        for (int i = 0; i < interfaceArray.length; i++) {
            Class interfaceType = interfaceArray[i];
            if (interfaceType == BeanPostProcessor.class) {//证明当前实例对象是后置处理器
                this.processorObj = (BeanPostProcessor) instance;
                break;
            }
        }
    }


    /**
     * 通过beanId获取对象
     *
     * @param beanId
     * @return
     * @throws Exception
     */
    public Object getBean(String beanId) throws Exception {
        Object instance = null;
        //当前实例对象的代理监控对象
        Object proxyObj = null;
        for (BeanDefined beanObj : beanDefinedList) {
            if (!beanId.equals(beanObj.getBeanId())) {
                continue;
            }
            String classPath = beanObj.getClassPath();
            Class classFile = Class.forName(classPath);
            String scope = beanObj.getScope();
            String factoryBean = beanObj.getFactoryBean();
            String factoryMehtod = beanObj.getFactoryMethod();
            if ("prototype".equals(scope)) {//.getBean每次都要返回一个全新实例对象
                if (factoryBean != null && factoryMehtod != null) {//用户希望使用指定工厂创建实例对象
                    Object factoryObj = SpringIoc.get(factoryBean);
                    Class factoryClass = factoryObj.getClass();
                    Method methodObj = factoryClass.getDeclaredMethod(factoryMehtod, null);
                    methodObj.setAccessible(true);
                    instance = methodObj.invoke(factoryObj, null);
                } else {
                    instance = classFile.newInstance();
                }
            } else {
                instance = SpringIoc.get(beanId);
            }

            if (this.processorObj != null) {
                proxyObj = this.processorObj.postProcessBeforeInitialization(instance, beanId);
                //实例对象初始化。Spring依赖注入
                proxyObj = this.processorObj.postProcessAfterInitialization(instance, beanId);
                //此时返回proxyObj可能就是原始bean对象，也有可能就是代理对象
                return proxyObj;
            } else {
                return instance;
            }
        }
        return null;
    }


    /**
     * 依赖注入
     *
     * @param instance
     * @param classFile
     * @param propertyMap
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public void setValue(Object instance, Class classFile, Map propertyMap) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //循环便利  propertyMap<属性名,属性值>
        Method methodArray[] = classFile.getDeclaredMethods();
        Set fieldNameSet = propertyMap.keySet();
        Iterator fieldIterator = fieldNameSet.iterator();
        while (fieldIterator.hasNext()) {
            String fieldName = (String) fieldIterator.next();
            String value = (String) propertyMap.get(fieldName);
            Field fieldObj = classFile.getDeclaredField(fieldName);//同名属性对象
            for (int i = 0; i < methodArray.length; i++) {
                Method methodObj = methodArray[i];
                String methodName = "set" + fieldName;// sid == setsid
                if (methodName.equalsIgnoreCase(methodObj.getName())) {
                    Class fieldType = fieldObj.getType();//属性的数据类型 Integer,String,Double,boolean,list
                    if (fieldType == String.class) {
                        methodObj.invoke(instance, value);
                    } else if (fieldType == Integer.class) {
                        methodObj.invoke(instance, Integer.valueOf(value));
                    } else if (fieldType == Boolean.class) {
                        methodObj.invoke(instance, Boolean.valueOf(value));
                    } else if (fieldType == List.class) {
                        List tempList = new ArrayList();
                        String dataArray[] = value.split(",");
                        for (int j = 0; j < dataArray.length; j++) {
                            tempList.add(dataArray[j]);
                        }
                        methodObj.invoke(instance, tempList);
                    } else { //此时属性类型是数组
                        String dataArray[] = value.split(",");
                        Object data[] = new Object[1];
                        data[0] = dataArray;
                        methodObj.invoke(instance, data);
                    }
                    break;
                }
            }
        }
    }
}
