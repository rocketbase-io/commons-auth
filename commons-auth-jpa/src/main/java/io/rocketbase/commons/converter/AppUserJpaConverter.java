package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.model.AppUserToken;

public class AppUserJpaConverter implements AppUserConverter<AppUserJpaEntity> {

    @Override
    public AppUserToken toToken(AppUserJpaEntity entity) {
        return null;
    }

    @Override
    public AppUserRead toRead(AppUserJpaEntity entity) {
        return null;
    }

}
