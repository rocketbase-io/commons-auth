package io.rocketbase.commons.service.impersonate;

import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.event.ImpersonateEvent;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.security.JwtTokenService;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

public class DefaultImpersonateService implements ImpersonateService {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public JwtTokenBundle getImpersonateBundle(AppUserToken requestedBy, AppUserToken impersonateAs) {
        applicationEventPublisher.publishEvent(new ImpersonateEvent(this, requestedBy, impersonateAs));

        return jwtTokenService.generateTokenBundle(impersonateAs);
    }
}
