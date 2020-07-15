package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppUserToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ImpersonateEvent extends ApplicationEvent {

    private final AppUserToken requestedBy;
    private final AppUserToken impersonateAs;

    public ImpersonateEvent(Object source, AppUserToken requestedBy, AppUserToken impersonateAs) {
        super(source);
        this.requestedBy = requestedBy;
        this.impersonateAs = impersonateAs;
    }
}
