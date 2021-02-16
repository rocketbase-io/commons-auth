package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RefreshTokenEvent extends ApplicationEvent {

    private final AppUserToken token;

    public RefreshTokenEvent(Object source, AppUserToken token) {
        super(source);
        this.token = token;
    }
}
