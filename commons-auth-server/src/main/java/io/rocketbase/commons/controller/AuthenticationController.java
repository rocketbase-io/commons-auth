package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.event.RefreshTokenEvent;
import io.rocketbase.commons.event.RequestMeEvent;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.user.ActiveUserStore;
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

    @Resource
    private ActiveUserStore activeUserStore;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/login", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody @NotNull @Validated LoginRequest login) {
        LoginResponse response = loginService.performLogin(login.getUsername(), login.getPassword());
        activeUserStore.addUser(response.getUser());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/auth/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AppUserRead> getAuthenticated(Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AppUserEntity entity = appUserService.getByUsername(authentication.getName());
        applicationEventPublisher.publishEvent(new RequestMeEvent(this, entity));
        activeUserStore.addUser(entity);
        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(value = "/auth/change-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@RequestBody @NotNull @Validated PasswordChangeRequest passwordChange, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        appUserService.performUpdatePassword(((CommonsAuthenticationToken) authentication).getUsername(), passwordChange);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/update-profile", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@RequestBody @NotNull @Validated UpdateProfileRequest updateProfile, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((CommonsAuthenticationToken) authentication).getUsername();
        appUserService.updateProfile(username, updateProfile);
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
        AppUserEntity entity = appUserService.getByUsername(((CommonsAuthenticationToken) authentication).getUsername());
        applicationEventPublisher.publishEvent(new RefreshTokenEvent(this, entity));
        activeUserStore.addUser(entity);
        return ResponseEntity.ok(jwtTokenService.generateAccessToken(entity));
    }
}
