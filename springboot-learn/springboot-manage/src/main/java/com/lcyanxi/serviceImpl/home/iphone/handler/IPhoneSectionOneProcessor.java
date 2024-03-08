package com.lcyanxi.serviceImpl.home.iphone.handler;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import com.lcyanxi.enums.IPhoneCardType;
import com.lcyanxi.home.HomeContext;
import com.lcyanxi.home.section.ICard;
import com.lcyanxi.home.CardType;
import com.lcyanxi.home.section.CardWithItems;
import com.lcyanxi.serviceImpl.home.AbstractCardTypeProcessor;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:51 下午
 */
@Component
public class IPhoneSectionOneProcessor extends AbstractCardTypeProcessor<ICard, HomeContext> {
    @Override
    public CardType supportCard() {
        return IPhoneCardType.SECTION_ONE;
    }


    @Override
    public ICard doBuildCard(HomeContext context) {
        List<Integer> data = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            data.add(i);
        }
        CardWithItems<Integer> items =
                new CardWithItems<>(IPhoneCardType.SECTION_ONE);
        items.setItems(data);
        return items;
    }
}
