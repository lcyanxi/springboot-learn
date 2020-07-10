package com.lcyanxi.limit.dictionary;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.experimental.Delegate;
import org.springframework.context.ApplicationEvent;

/**
 * @author lichang
 * @date 2020/7/9
 */
public class ApolloChangeEvent extends ApplicationEvent {

    @Delegate
    private ConfigChangeEvent configChangeEvent;

    /**
     * Create a new ApplicationEvent.
     *
     * @param configChangeEvent the component that published the event (never {@code null})
     */
    public ApolloChangeEvent(ConfigChangeEvent configChangeEvent) {
        super(configChangeEvent);
        this.configChangeEvent = configChangeEvent;
    }


}
