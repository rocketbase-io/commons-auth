package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import org.springframework.data.domain.Pageable;

public interface AppUserApi {

    PageableResult<AppUserRead> find(Pageable pageable);

    PageableResult<AppUserRead> find(QueryAppUser query, Pageable pageable);

    AppUserRead create(AppUserCreate create);

    AppUserRead patch(String id, AppUserUpdate update);

    void delete(String id);

    AppInviteRead invite(InviteRequest inviteRequest);

}
