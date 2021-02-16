package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailChangeEvent extends ApplicationEvent {

    private final String oldEmailAddress;
    private final AppUserEntity appUser;


    public EmailChangeEvent(Object source, String oldEmailAddress, AppUserEntity appUser) {
        super(source);
        this.oldEmailAddress = oldEmailAddress;
        this.appUser = appUser;
    }
}
