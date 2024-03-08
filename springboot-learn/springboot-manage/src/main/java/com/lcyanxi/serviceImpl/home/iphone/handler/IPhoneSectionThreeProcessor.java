package com.lcyanxi.serviceImpl.home.iphone.handler;

import java.util.List;

import com.lcyanxi.serviceImpl.home.AbstractCardTypeProcessor;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.lcyanxi.enums.IPhoneCardType;
import com.lcyanxi.home.HomeContext;
import com.lcyanxi.home.section.ICard;
import com.lcyanxi.home.CardType;
import com.lcyanxi.home.section.CardWithItems;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:51 下午
 */
@Component
public class IPhoneSectionThreeProcessor extends AbstractCardTypeProcessor<ICard, HomeContext> {
    @Override
    public CardType supportCard() {
        return IPhoneCardType.SECTION_THREE;
    }


    @Override
    public ICard doBuildCard(HomeContext context) {
        List<String> data = Lists.newArrayList("张三", "李四", "王武", "融融");
        CardWithItems<String> items =
                new CardWithItems<>(IPhoneCardType.SECTION_THREE);
        items.setItems(data);
        return items;
    }
}
