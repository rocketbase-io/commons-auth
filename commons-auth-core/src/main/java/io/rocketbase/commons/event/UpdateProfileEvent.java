package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateProfileEvent extends ApplicationEvent {

    private final AppUserEntity appUserEntity;

    public UpdateProfileEvent(Object source, AppUserEntity appUserEntity) {
        super(source);
        this.appUserEntity = appUserEntity;
    }
}
