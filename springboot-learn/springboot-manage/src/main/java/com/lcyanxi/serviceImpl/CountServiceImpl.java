package com.lcyanxi.serviceImpl;

import com.lcyanxi.service.ICountService;

/**
 * @author lichang
 * @date 2020/12/5
 */
public class CountServiceImpl implements ICountService {
    private int count = 0;
    @Override
    public int count() {
        return count ++;
    }
}
