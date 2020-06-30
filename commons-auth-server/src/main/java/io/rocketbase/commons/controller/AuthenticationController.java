package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.event.ChangePasswordEvent;
import io.rocketbase.commons.event.UpdateProfileEvent;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.user.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class AuthenticationController {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/login", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody @NotNull @Validated LoginRequest login) {
        return ResponseEntity.ok(loginService.performLogin(login.getUsername(), login.getPassword()));
    }

    @RequestMapping(value = "/auth/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AppUserRead> getAuthenticated(Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(appUserConverter.fromEntity(appUserService.getByUsername(authentication.getName())));
    }

    @RequestMapping(value = "/auth/change-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@RequestBody @NotNull @Validated PasswordChangeRequest passwordChange, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AppUserEntity entity = appUserService.performUpdatePassword(((CommonsAuthenticationToken) authentication).getUsername(), passwordChange);

        applicationEventPublisher.publishEvent(new ChangePasswordEvent(this, entity));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/update-profile", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@RequestBody @NotNull @Validated UpdateProfileRequest updateProfile, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((CommonsAuthenticationToken) authentication).getUsername();

        appUserService.updateProfile(username, updateProfile);

        applicationEventPublisher.publishEvent(new UpdateProfileEvent(this, appUserService.getByUsername(username)));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/refresh", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> refreshToken(Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getAuthorities() == null || !authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(JwtTokenService.REFRESH_TOKEN))) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        AppUserEntity appUser = appUserService.getByUsername(((CommonsAuthenticationToken) authentication).getUsername());

        return ResponseEntity.ok(jwtTokenService.generateAccessToken(appUser));
    }
}
