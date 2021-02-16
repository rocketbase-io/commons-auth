package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationEvent extends ApplicationEvent {

    private final AppUserEntity appUser;
    private final RegistrationProcessType type;

    public RegistrationEvent(Object source, AppUserEntity appUser, RegistrationProcessType type) {
        super(source);
        this.appUser = appUser;
        this.type = type;
    }

    public enum RegistrationProcessType {
        REGISTER,
        VERIFIED
    }
}
