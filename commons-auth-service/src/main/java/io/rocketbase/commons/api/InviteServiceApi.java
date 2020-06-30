package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.invite.AppInviteService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InviteServiceApi implements InviteApi {

    private final AppInviteService appInviteService;
    private final AppInviteConverter inviteConverter;
    private final AppUserConverter userConverter;

    @Override
    public AppInviteRead verify(String inviteId) {
        return inviteConverter.fromEntity(appInviteService.verifyInvite(inviteId));
    }

    @Override
    public AppUserRead transformToUser(ConfirmInviteRequest confirmInvite) {
        AppUserEntity userEntity = appInviteService.confirmInvite(confirmInvite);
        return userConverter.fromEntity(userEntity);
    }
}
