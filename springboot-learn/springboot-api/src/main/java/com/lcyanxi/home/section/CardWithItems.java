package com.lcyanxi.home.section;

import java.util.List;

import com.lcyanxi.home.CardType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/5:11 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CardWithItems<T> extends BaseCard {

    private List<T> items;

    private String alias = "items";

    public CardWithItems(CardType sectionType) {
        super(sectionType);
    }

    public void withName(String alias) {
        this.alias = alias;
    }
}
