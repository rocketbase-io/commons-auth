package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UsernameChangeEvent extends ApplicationEvent {

    private final String oldUsername;
    private final AppUserEntity appUserEntity;


    public UsernameChangeEvent(Object source, String oldUsername, AppUserEntity appUserEntity) {
        super(source);
        this.oldUsername = oldUsername;
        this.appUserEntity = appUserEntity;
    }
}
