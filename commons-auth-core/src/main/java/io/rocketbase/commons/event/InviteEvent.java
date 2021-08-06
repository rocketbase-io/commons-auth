package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.annotation.Nullable;

@Getter
public class InviteEvent extends ApplicationEvent {

    private final AppInviteEntity appInvite;
    private final InviteProcessType type;

    /**
     * only filled in case on processType confirm
     */
    @Nullable
    private AppUserEntity appUser;

    public InviteEvent(Object source, AppInviteEntity appInvite, InviteProcessType type) {
        super(source);
        this.appInvite = appInvite;
        this.type = type;
    }

    public InviteEvent(Object source, AppInviteEntity appInvite, AppUserEntity appUser) {
        super(source);
        this.appInvite = appInvite;
        this.type = InviteProcessType.CONFIRM;
        this.appUser = appUser;
    }

    public enum InviteProcessType {
        CREATE,
        VERIFY,
        CONFIRM
    }
}
