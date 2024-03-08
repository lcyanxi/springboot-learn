package com.lcyanxi.home;

import com.lcyanxi.home.section.ICard;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/4:00 下午
 */
public interface CardTypeProcessor<T extends ICard, C extends HomeContext> {
    CardType supportCard();

    List<CardType> supportCards();

    T doBuildCard(C context);

    List<T> doBuildCards(C context);
}
