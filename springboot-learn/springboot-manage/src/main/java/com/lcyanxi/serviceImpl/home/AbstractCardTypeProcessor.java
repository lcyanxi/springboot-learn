package com.lcyanxi.serviceImpl.home;

import com.google.common.collect.Lists;
import com.lcyanxi.home.HomeContext;
import com.lcyanxi.home.section.ICard;
import com.lcyanxi.home.CardType;
import com.lcyanxi.home.CardTypeProcessor;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:56 下午
 */
public abstract class AbstractCardTypeProcessor<T extends ICard, C extends HomeContext>
        implements
        CardTypeProcessor<T, C> {
    @Override
    public CardType supportCard() {
        return null;
    }

    @Override
    public List<T> doBuildCards(C context) {
        T t = doBuildCard(context);
        if (t != null) {
            return Lists.newArrayList(t);
        }
        return Lists.newArrayList();
    }

    @Override
    public T doBuildCard(C context) {
        return null;
    }

    public List<CardType> supportCards() {
        CardType cardType = supportCard();
        if (cardType != null) {
            return Lists.newArrayList(cardType);
        }
        return Lists.newArrayList();
    }
}
