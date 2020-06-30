package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.CommonsPrincipal;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImpersonateServiceApi implements ImpersonateApi {

    private final ImpersonateService impersonateService;
    private final AppUserService appUserService;

    @Override
    public JwtTokenBundle impersonate(String userIdOrUsername) {
        AppUserEntity entity = appUserService.findByIdOrUsername(userIdOrUsername).orElseThrow(NotFoundException::new);
        return impersonateService.getImpersonateBundle(CommonsPrincipal.getCurrent(), entity);
    }
}
