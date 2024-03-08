package com.lcyanxi.serviceImpl.home;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.lcyanxi.enums.IPhoneCardType;
import com.lcyanxi.home.section.CardWithItems;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lcyanxi.enums.HomePageType;
import com.lcyanxi.home.CardType;
import com.lcyanxi.home.CardTypeProcessor;
import com.lcyanxi.home.HomeContext;
import com.lcyanxi.home.HomePageInfo;
import com.lcyanxi.home.section.ICard;

import lombok.extern.log4j.Log4j;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:52 下午
 */
@Service
@Log4j
public abstract class AbstractHomePageService<C extends HomeContext, D> implements HomePageService {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 6, 3,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(3),
            new ThreadFactoryBuilder().setNameFormat("home_page_%d").build());

    @Override
    public HomePageInfo<?> getHomePageInfo(String req) {
        // 构建上下文参数
        C context = buildHomeContext(req);
        if (context == null) {
            return null;
        }
        // 多线程获取 sections 数据
        List<ICard> cards = fetchCards(context);
        // 拼装返回结果
        HomePageInfo<D> view = new HomePageInfo<>();
        view.setCards(cards);
        view.setDeviceInfo(buildDeviceInfo(context));
        return view;
    }

    // 上下文参数
    protected abstract C buildHomeContext(String req);

    // card 名称列表
    protected abstract List<CardType> getCardTypes(C context);

    // processor 列表
    protected abstract List<CardTypeProcessor<ICard, C>> getCardTypeProcessors(C context);

    // 由子类构建 meta 信息
    protected D buildDeviceInfo(C context) {
        return null;
    }

    /**
     * 构建公共部分数据
     */
    protected HomeContext buildCommonContext(String req) {
        return HomeContext.builder().pageType(HomePageType.IPHONE).userId(req).build();
    }


    private List<ICard> fetchCards(C context) {
        // 获取要那些 section 类型
        List<CardType> cardTypes = getCardTypes(context);
        if (CollectionUtils.isEmpty(cardTypes)) {
            return new ArrayList<>();
        }
        // 获取每个 section 类型具体的执行器
        List<CardTypeProcessor<ICard, C>> processors = getCardTypeProcessors(context);
        Set<String> cardTypeNames = cardTypes.stream().map(CardType::getType).collect(Collectors.toSet());

        List<Future<ICard>> list = processors.stream()
                .filter(item -> item != null && cardTypeNames.contains(item.supportCard().getType()))
                .map(processor -> threadPool.submit(() -> processor.doBuildCard(context))).collect(Collectors.toList());

        List<ICard> cards = Lists.newArrayList();
        list.forEach(future -> {
            try {
                ICard card = future.get(1000, TimeUnit.MILLISECONDS);
                if (Objects.isNull(card)) {
                    return;
                }
                if (card instanceof CardWithItems && IPhoneCardType.SECTION_THREE.getType().equals(card.getType())) {
                    CardWithItems cardWithItems = (CardWithItems) card;
                    cardWithItems.withName("iphone_card_three");
                }
                cards.add(card);
            } catch (Exception e) {}
        });
        return cards.stream().sorted(Comparator.comparing(ICard::getIndex)).collect(Collectors.toList());
    }

}
