package com.lcyanxi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/07/15/11:53 上午
 */
@Slf4j
public class MyApplicationListener implements InitializingBean, ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("+++++++++++++++++++++++");
        if (event instanceof ContextStartedEvent) {
            log.info("=============:{}", "应用开启事件: ContextStartedEvent");
        }
        if (event instanceof ContextRefreshedEvent) {
            log.info("============:{}", "应用刷新事件: ContextRefreshedEvent");
        }
        if (event instanceof ContextClosedEvent) {
            log.info("=========:{}", "应用关闭事件: ContextClosedEvent");
        }
        if (event instanceof ContextStoppedEvent) {
            log.info("=========:{}", "应用停止事件: ContextStoppedEvent");
        }
        if (event instanceof ApplicationReadyEvent) {
            log.info("=========:{}", "spring Application启动完成事件: ApplicationReadyEvent");
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>:{}\n", event.getClass().getSimpleName());
    }

    /**
     * Bean 注入到 Spring 容器且初始化后，执行特定业务化的操作
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("MyApplicationListener Initializing .........");
    }
}
