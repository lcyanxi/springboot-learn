package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.ICountService;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lichang
 * @date 2020/12/5
 */
@Slf4j
public class CountServiceImpl implements ICountService {
    private int count = 0;
    @Override
    public int count() {
        System.out.println(LocalTime.now() + ": Java类的热加载");
        return count ++;
    }
}
