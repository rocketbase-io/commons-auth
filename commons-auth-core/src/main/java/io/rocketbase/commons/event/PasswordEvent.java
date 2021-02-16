package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordEvent extends ApplicationEvent {

    private final AppUserEntity appUser;
    private final PasswordProcessType type;

    public PasswordEvent(Object source, AppUserEntity appUser, PasswordProcessType type) {
        super(source);
        this.appUser = appUser;
        this.type = type;
    }

    public enum PasswordProcessType {
        REQUEST_RESET,
        PROCEED_RESET,
        CHANGED
    }
}
