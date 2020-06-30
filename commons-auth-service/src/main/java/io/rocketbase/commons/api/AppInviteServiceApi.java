package io.rocketbase.commons.api;

import io.rocketbase.commons.converter.AppInviteConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.service.invite.AppInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AppInviteServiceApi implements AppInviteApi, BaseServiceApi {

    private final AppInviteService appInviteService;
    private final AppInviteConverter converter;

    @Override
    public PageableResult<AppInviteRead> find(QueryAppInvite query, Pageable pageable) {
        Page<AppInviteEntity> page = appInviteService.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public AppInviteRead invite(InviteRequest inviteRequest) {
        return converter.fromEntity(appInviteService.createInvite(inviteRequest, getBaseUrl()));
    }

    @Override
    public void delete(String id) {
        appInviteService.deleteInvite(id);
    }
}
