package com.lcyanxi.enums;


/**
 * @author lichang
 * @description: rocket topic 信息
 * @date 2020/04/14
 */

public enum RocketTopicInfoEnum {


    /**
     * 合班队列信息
     */
    USER_LESSON_TOPIC("user_lesson", "springboot_manage_user_lesson_topic",
            "com.lcyanxi.consumer.UserLessonConsumer"),

    /**
     * 合班队列信息
     */
    SEND_DEDUP_TOPIC("dedup", "springboot_manage_dedup_topic",
            "com.lcyanxi.consumer.DedupDemoConsumer"),

    /**
     * 顺序消费队列消息
     */
    ORDERLY_TOPIC("orderly", "orderly_topic", "com.lcyanxi.consumer.UserLessonConsumer");


    private String key;

    private String className;

    private String topic;

    RocketTopicInfoEnum(String key, String topic, String className) {
        this.key = key;
        this.topic = topic;
        this.className = className;
    }

    public String getKey() {
        return key;
    }

    public String getTopic() {
        return topic;
    }

    public String getClassName() {
        return className;
    }

}
