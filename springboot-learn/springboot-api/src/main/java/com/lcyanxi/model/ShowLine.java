package com.lcyanxi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : lichang
 * @desc : 描述信息
 * @since : 2022/03/10/6:34 下午
 */
@Data
@Builder
public class ShowLine {
    private String metaType;

    private List<LineInfo> infos;

    @Data
    @Builder
    public static class LineInfo {
        private int id;

        private List<String> showType;
    }
}
