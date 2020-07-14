package com.lcyanxi.spring;

public interface BeanPostProcessor {

    /**
     * 前置处理器
     * @param bean 对象
     * @param beanName 对象名称
     * @return
     * @throws Exception
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * 后置处理器
     * @param bean 对象
     * @param beanName 对象名称
     * @return
     * @throws Exception
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
