package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordEvent extends ApplicationEvent {

    private final AppUserEntity appUserEntity;
    private final PasswordProcessType type;

    public PasswordEvent(Object source, AppUserEntity appUserEntity, PasswordProcessType type) {
        super(source);
        this.appUserEntity = appUserEntity;
        this.type = type;
    }

    public enum PasswordProcessType {
        REQUEST_RESET,
        PROCEED_RESET,
        CHANGED
    }
}
