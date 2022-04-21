package com.lcyanxi.basics.designPatterns.template;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/04/21/6:12 下午
 */
public class SettingDemo {
    public static void main(String[] args) {
        AbstractSetting setting1 = new LocalSetting();
        System.out.println("test = " + setting1.getSetting("test"));
        System.out.println("test = " + setting1.getSetting("test"));
    }
}
