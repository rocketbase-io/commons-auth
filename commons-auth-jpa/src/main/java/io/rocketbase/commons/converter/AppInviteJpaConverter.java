package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.model.AppInviteJpaEntity;

public class AppInviteJpaConverter implements AppInviteConverter<AppInviteJpaEntity> {

    @Override
    public AppInviteRead toRead(AppInviteJpaEntity entity) {
        return null;
    }
}
