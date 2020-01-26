package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoginEvent extends ApplicationEvent {

    private AppUserEntity appUserEntity;

    public LoginEvent(Object source, AppUserEntity appUserEntity) {
        super(source);
        this.appUserEntity = appUserEntity;
    }
}
