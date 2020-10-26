package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appuser.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface AppUserApi {

    @Deprecated
    default PageableResult<AppUserRead> find(int page, int pagesize) {
        return find(PageRequest.of(page, pagesize));
    }

    default PageableResult<AppUserRead> find(Pageable pageable) {
        return find(null, pageable);
    }

    PageableResult<AppUserRead> find(QueryAppUser query, Pageable pageable);

    AppUserRead create(AppUserCreate create);

    AppUserRead resetPassword(String usernameOrId, AppUserResetPassword reset);

    AppUserRead patch(String usernameOrId, AppUserUpdate update);

    void delete(String id);

    AppInviteRead invite(InviteRequest inviteRequest);

}
