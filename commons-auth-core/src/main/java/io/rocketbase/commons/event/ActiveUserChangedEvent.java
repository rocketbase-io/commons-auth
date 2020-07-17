package io.rocketbase.commons.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * will get trigged when amount of ActiveUserStore is changed
 */
@Getter
public class ActiveUserChangedEvent extends ApplicationEvent {

    public ActiveUserChangedEvent(Object source) {
        super(source);
    }
}
