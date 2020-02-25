package io.rocketbase.commons.service.impersonate;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;

import javax.annotation.Resource;

public class DefaultImpersonateService implements ImpersonateService {

    @Resource
    private JwtTokenService jwtTokenService;

    @Override
    public JwtTokenBundle getImpersonateBundle(AppUserToken requestedBy, AppUserToken impersonateAs) {
        return jwtTokenService.generateTokenBundle(impersonateAs);
    }
}
