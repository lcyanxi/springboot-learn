package com.lcyanxi.bytebuddy;


/**
 * @author SuyuZhuang
 * @date 2019/10/17 10:44 下午
 */
public class MyService {

    @MyAnnotation
    public void queryDatabase() {
        System.out.println("query db:");
    }

    @MyAnnotation
    public void provideHttpResponse() {
        System.out.println("provide response:");
    }

    public void noLog() {
        System.out.println("no log");
    }
}
