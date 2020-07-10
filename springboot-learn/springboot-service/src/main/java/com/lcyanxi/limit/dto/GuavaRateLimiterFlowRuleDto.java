package com.lcyanxi.limit.dto;

import java.io.Serializable;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lichang
 * @date 2020/7/9
 */
@Data
public class GuavaRateLimiterFlowRuleDto implements Serializable {
    /**
     * 资源名
     */
    private String resource;

    /**
     * 限制qps
     */
    private Integer qps;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 限流app名称
     */
    private String limitApp;


    public boolean hasSuccess() {
        return StringUtils.isNotBlank(resource) && qps > 0 && enable && StringUtils.isNotBlank(limitApp);
    }
}
