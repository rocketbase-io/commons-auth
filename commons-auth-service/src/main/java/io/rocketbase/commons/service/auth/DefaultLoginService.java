package io.rocketbase.commons.service.auth;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.event.LoginEvent;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Resource;

public class DefaultLoginService implements LoginService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public LoginResponse performLogin(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username.toLowerCase(), password)
        );
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        AppUserEntity user = appUserService.updateLastLogin(((UserDetails)authentication.getPrincipal()).getUsername());

        applicationEventPublisher.publishEvent(new LoginEvent(this, user));

        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(user);
        return new LoginResponse(jwtTokenBundle, appUserConverter.fromEntity(user));
    }
}
