package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import org.springframework.data.domain.Pageable;

public interface AppInviteApi {

    PageableResult<AppInviteRead> find(QueryAppInvite query, Pageable pageable);

    AppInviteRead invite(InviteRequest inviteRequest);

    void delete(Long id);

}
