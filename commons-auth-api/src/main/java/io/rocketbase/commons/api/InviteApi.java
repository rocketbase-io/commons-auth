package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;

public interface InviteApi {

    AppInviteRead verify(String inviteId);

    AppUserRead transformToUser(ConfirmInviteRequest confirmInvite);

}
