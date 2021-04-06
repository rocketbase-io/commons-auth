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

import java.util.Optional;

@RequiredArgsConstructor
public class AppInviteApiService implements AppInviteApi, BaseApiService {

    private final AppInviteService appInviteService;
    private final AppInviteConverter converter;

    @Override
    public PageableResult<AppInviteRead> find(QueryAppInvite query, Pageable pageable) {
        Page<AppInviteEntity> page = appInviteService.findAll(query, pageable);
        return PageableResult.contentPage(converter.fromEntities(page.getContent()), page);
    }

    @Override
    public Optional<AppInviteRead> findById(Long id) {
        Optional<AppInviteEntity> optional = appInviteService.findById(id);
        return optional.isPresent() ? Optional.of(converter.fromEntity(optional.get())) : Optional.empty();
    }

    @Override
    public AppInviteRead invite(InviteRequest inviteRequest) {
        return converter.fromEntity(appInviteService.createInvite(inviteRequest, getBaseUrl()));
    }

    @Override
    public void delete(Long id) {
        appInviteService.deleteInvite(id);
    }
}
