package com.lcyanxi.dedup.persist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by lichang
 */
@AllArgsConstructor
@Getter
@ToString
public class DedupElement {
    /**
     * 应用名称
     */
    private String application;

    /**
     * topic
     */
    private String topic;

    /**
     * tag
     */
    private String tag;

    /**
     * 唯一key
     */
    private String msgUniqKey;

}
