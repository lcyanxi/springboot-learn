package com.lcyanxi.jvm.myClassLoader;

import com.lcyanxi.service.ICountService;

/**
 * @author lichang
 * @date 2020/12/22
 */
public class MsgHandle implements Runnable {
    @Override
    public void run() {
        while (true) {
            ICountService manager = ManagerFactory.getManager(ManagerFactory.MY_MANAGER);
            manager.count();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
