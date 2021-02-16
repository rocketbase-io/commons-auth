package io.rocketbase.commons.api;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.CommonsPrincipal;
import io.rocketbase.commons.service.impersonate.ImpersonateService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImpersonateApiService implements ImpersonateApi {

    private final ImpersonateService impersonateService;
    private final AppUserTokenService appUserTokenService;

    @Override
    public JwtTokenBundle impersonate(String userIdOrUsername) {
        AppUserToken token = appUserTokenService.findByIdOrUsername(userIdOrUsername).orElseThrow(NotFoundException::new);
        return impersonateService.getImpersonateBundle(CommonsPrincipal.getCurrent(), token);
    }
}
