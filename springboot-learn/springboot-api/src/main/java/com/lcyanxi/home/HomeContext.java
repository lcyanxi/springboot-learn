package com.lcyanxi.home;

import com.lcyanxi.enums.HomePageType;
import lombok.Builder;
import lombok.Data;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/07/3:53 下午
 */
@Builder
@Data
public class HomeContext {
    private String userId;

    private HomePageType pageType;
}
