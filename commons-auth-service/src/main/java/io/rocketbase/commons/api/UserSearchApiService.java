package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserSearchApiService implements UserSearchApi {

    private final AppUserService appUserService;

    @Override
    public PageableResult<AppUserReference> search(QueryAppUser query, Pageable pageable) {
        Page<AppUserEntity> page = appUserService.findAll(query, pageable);
        return PageableResult.contentPage(page.stream().map(AppUserEntity::toReference).collect(Collectors.toList()), page);
    }

    @Override
    public Optional<AppUserReference> findByUsernameOrId(String usernameOrId) {
        Optional<AppUserEntity> optional = appUserService.findByIdOrUsername(usernameOrId);
        return optional.isPresent() ? Optional.of(optional.get().toReference()) : Optional.empty();
    }
}
