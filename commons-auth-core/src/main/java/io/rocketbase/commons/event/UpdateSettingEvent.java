package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateSettingEvent extends ApplicationEvent {

    private final AppUserEntity appUser;

    public UpdateSettingEvent(Object source, AppUserEntity appUser) {
        super(source);
        this.appUser = appUser;
    }
}
