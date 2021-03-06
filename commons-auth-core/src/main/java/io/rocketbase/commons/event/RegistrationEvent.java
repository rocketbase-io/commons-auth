package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationEvent extends ApplicationEvent {

    private final AppUserEntity appUserEntity;
    private final RegistrationProcessType type;

    public RegistrationEvent(Object source, AppUserEntity appUserEntity, RegistrationProcessType type) {
        super(source);
        this.appUserEntity = appUserEntity;
        this.type = type;
    }

    public enum RegistrationProcessType {
        REGISTER,
        VERIFIED
    }
}
