package com.lcyanxi.serviceImpl.home;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.lcyanxi.enums.HomePageType;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:26 下午
 */
@Service
public class HomePageServiceFactory {
    @Resource
    private List<HomePageService> homePageServices;

    private Map<HomePageType, HomePageService> sectionServiceMap = Maps.newConcurrentMap();

    @PostConstruct
    private void init() {
        sectionServiceMap = homePageServices.stream()
                .collect(Collectors.toMap(HomePageService::getHomePageType, Function.identity()));
    }

    public HomePageService getHomePageService(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        HomePageType pageType = HomePageType.parseByType(type);
        if (pageType == null) {
            return null;
        }
        return sectionServiceMap.get(pageType);
    }
}
