package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.annotation.Nullable;

@Getter
public class InviteEvent extends ApplicationEvent {

    private final AppInviteEntity appInviteEntity;
    private final InviteProcessType type;

    /**
     * only filled in case on processType confirm
     */
    @Nullable
    private AppUserEntity appUserEntity;

    public InviteEvent(Object source, AppInviteEntity appInviteEntity, InviteProcessType type) {
        super(source);
        this.appInviteEntity = appInviteEntity;
        this.type = type;
    }

    public InviteEvent(Object source, AppInviteEntity appInviteEntity, AppUserEntity appUserEntity) {
        super(source);
        this.appInviteEntity = appInviteEntity;
        this.type = InviteProcessType.CONFIRM;
        this.appUserEntity = appUserEntity;
    }

    public enum InviteProcessType {
        CREATE,
        VERIFY,
        CONFIRM
    }
}
