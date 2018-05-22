package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RegistrationEvent extends ApplicationEvent {

    private AppUser appUser;

    public RegistrationEvent(Object source, AppUser appUser) {
        super(source);
        this.appUser = appUser;
    }
}
