package com.lcyanxi.home.section;

import com.lcyanxi.home.CardType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/5:06 下午
 */
@Data
@NoArgsConstructor
public class BaseCard implements ICard {
    private String type;

    private int index;

    private String cardName;

    public BaseCard(CardType type) {
        this.type = type.getType();
        this.index = type.getIndex();
        this.cardName = type.getName();
    }
}
