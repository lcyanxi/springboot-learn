package com.lcyanxi.home;

import com.lcyanxi.home.section.ICard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:31 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageInfo<DEVICE> {
    private DEVICE deviceInfo;

    private List<? extends ICard> cards;
}
