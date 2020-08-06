package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailChangeEvent extends ApplicationEvent {

    private final String oldEmailAddress;
    private final AppUserEntity appUserEntity;


    public EmailChangeEvent(Object source, String oldEmailAddress, AppUserEntity appUserEntity) {
        super(source);
        this.oldEmailAddress = oldEmailAddress;
        this.appUserEntity = appUserEntity;
    }
}
