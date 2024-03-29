package com.lcyanxi.finish;

import lombok.Data;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2024/03/11/6:18 下午
 */
@Data
public class FinishPageRsp<BASICINFO> {
    private BASICINFO BasicInfo;

    private List<? extends ISection> sections;
}
