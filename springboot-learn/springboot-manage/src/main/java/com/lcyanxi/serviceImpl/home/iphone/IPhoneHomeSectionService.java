package com.lcyanxi.serviceImpl.home.iphone;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lcyanxi.enums.HomePageType;
import com.lcyanxi.enums.IPhoneCardType;
import com.lcyanxi.home.CardType;
import com.lcyanxi.home.CardTypeProcessor;
import com.lcyanxi.home.DeviceInfo;
import com.lcyanxi.home.HomeContext;
import com.lcyanxi.home.section.ICard;
import com.lcyanxi.serviceImpl.home.AbstractHomePageService;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:27 下午
 */
@Service
public class IPhoneHomeSectionService extends AbstractHomePageService<HomeContext, DeviceInfo> {
    @Autowired
    private List<CardTypeProcessor<ICard, HomeContext>> handlers;

    @Override
    protected HomeContext buildHomeContext(String req) {
        return super.buildCommonContext(req);
    }

    @Override
    protected List<CardType> getCardTypes(HomeContext context) {
        return Arrays.stream(IPhoneCardType.values()).collect(Collectors.toList());
    }

    @Override
    protected List<CardTypeProcessor<ICard, HomeContext>> getCardTypeProcessors(HomeContext context) {
        return handlers;
    }

    @Override
    public HomePageType getHomePageType() {
        return HomePageType.IPHONE;
    }

    @Override
    protected DeviceInfo buildDeviceInfo(HomeContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DeviceInfo.builder().sn("20240307").mac("2011110101").bindDate(sdf.format(new Date())).build();
    }
}
