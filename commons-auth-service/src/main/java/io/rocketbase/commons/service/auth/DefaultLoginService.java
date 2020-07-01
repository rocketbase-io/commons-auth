package io.rocketbase.commons.service.auth;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.event.LoginEvent;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

public class DefaultLoginService implements LoginService {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private PasswordEncoder passwordEncoder;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public LoginResponse performLogin(String username, String password) {
        UserDetails userDetails = appUserService.loadUserByUsername(username);

        userDetailsChecker.check(userDetails);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Credentials");
        }

        AppUserEntity user = appUserService.updateLastLogin(userDetails.getUsername());

        applicationEventPublisher.publishEvent(new LoginEvent(this, user));

        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(user);
        return new LoginResponse(jwtTokenBundle, appUserConverter.fromEntity(user));
    }
}
