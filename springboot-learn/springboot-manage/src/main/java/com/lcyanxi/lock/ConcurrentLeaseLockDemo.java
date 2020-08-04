package com.lcyanxi.lock;

import com.lcyanxi.util.ReentrantLockDemo;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/8/4
 */
@Slf4j
public class ConcurrentLeaseLockDemo {
    public static void main(String[] args) {

        for (int i = 0; i < 5; i++){
            new Thread(new ConcurrentLeaseLockDemo.ThreadDemo(i)).start();
        }

    }


    static class ThreadDemo implements Runnable {
        Integer id;

        public ThreadDemo(Integer id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @ConcurrentLeaseLock(lockKey = "eduSGOpenCourseHandle:lock")
    private static void execute() throws InterruptedException {
        log.info("execute start-----------------");
        TimeUnit.MILLISECONDS.sleep(10);
        log.info("execute end-----------------");
    }

}
