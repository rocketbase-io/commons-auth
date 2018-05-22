package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateProfileEvent extends ApplicationEvent {

    private AppUser appUser;

    public UpdateProfileEvent(Object source, AppUser appUser) {
        super(source);
        this.appUser = appUser;
    }
}
