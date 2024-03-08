package com.lcyanxi.serviceImpl.home;

import com.lcyanxi.enums.HomePageType;
import com.lcyanxi.home.HomePageInfo;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:29 下午
 */
public interface HomePageService {
    HomePageInfo<?> getHomePageInfo(String req);

    HomePageType getHomePageType();
}
