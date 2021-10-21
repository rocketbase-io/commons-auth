package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.dto.authentication.*;
import io.rocketbase.commons.event.RefreshTokenEvent;
import io.rocketbase.commons.event.RequestMeEvent;
import io.rocketbase.commons.event.UpdateProfileEvent;
import io.rocketbase.commons.event.UpdateSettingEvent;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.user.UserProfile;
import io.rocketbase.commons.model.user.UserSetting;
import io.rocketbase.commons.security.CommonsAuthenticationToken;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.auth.LoginService;
import io.rocketbase.commons.service.capability.AppCapabilityService;
import io.rocketbase.commons.service.change.ChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.client.AppClientService;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.service.user.AppUserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
public class AuthenticationController implements BaseController {

    @Resource
    private JwtTokenService jwtTokenService;

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserTokenService appUserTokenService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private LoginService loginService;

    @Resource
    private ActiveUserStore activeUserStore;

    @Resource
    private ChangeAppUserWithConfirmService changeAppUserWithConfirmService;

    @Resource
    private AppClientService appClientService;

    @Resource
    private AppCapabilityService appCapabilityService;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/login", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@RequestBody @NotNull @Validated LoginRequest login) {
        LoginResponse response = loginService.performLogin(login.getUsername(), login.getPassword());
        activeUserStore.addUser(response.getUser());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/auth/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AppUserToken> getAuthenticated(Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AppUserEntity entity = appUserService.getByUsername(authentication.getName());
        applicationEventPublisher.publishEvent(new RequestMeEvent(this, entity));
        activeUserStore.addUser(appUserTokenService.lookup(entity));
        return ResponseEntity.ok(appUserConverter.toToken(entity)
                .retainCapabilities(((CommonsAuthenticationToken) authentication).getCapabilities()));
    }

    @RequestMapping(value = "/auth/change-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtTokenBundle> changePassword(@RequestBody @NotNull @Validated PasswordChangeRequest passwordChange, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AppUserEntity entity = appUserService.performUpdatePassword(((CommonsAuthenticationToken) authentication).getUsername(), passwordChange);
        return ResponseEntity.ok(jwtTokenService.generateTokenBundle(appUserConverter.toToken(entity)));
    }

    @RequestMapping(value = "/auth/change-username", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserRead> changeUsername(@RequestBody @NotNull @Validated UsernameChangeRequest usernameChange, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AppUserEntity entity = appUserService.changeUsername(((CommonsAuthenticationToken) authentication).getId(), usernameChange.getNewUsername());
        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(value = "/auth/change-email", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpirationInfo<AppUserRead>> changeEmail(HttpServletRequest request, @RequestBody @NotNull @Validated EmailChangeRequest emailChange, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ExpirationInfo<AppUserEntity> expirationInfo = changeAppUserWithConfirmService.handleEmailChangeRequest(((CommonsAuthenticationToken) authentication).getId(), emailChange, getBaseUrl(request));
        return ResponseEntity.ok(ExpirationInfo.<AppUserRead>builder()
                .expires(expirationInfo.getExpires())
                .detail(appUserConverter.fromEntity(expirationInfo.getDetail()))
                .build());
    }

    @RequestMapping(value = "/auth/verify-email", method = RequestMethod.GET)
    public ResponseEntity<AppUserRead> changeEmail(@RequestParam("verification") String verification) {
        AppUserEntity entity = changeAppUserWithConfirmService.confirmEmailChange(verification);
        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(value = "/auth/update-profile", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserRead> updateProfile(@RequestBody @NotNull @Validated UserProfile profile, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((CommonsAuthenticationToken) authentication).getUsername();
        AppUserEntity entity = appUserService.patch(username, AppUserUpdate.builder().profile(profile).build());
        applicationEventPublisher.publishEvent(new UpdateProfileEvent(this, entity));

        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(value = "/auth/update-setting", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserRead> updateProfile(@RequestBody @NotNull @Validated UserSetting setting, Authentication authentication) {
        if (authentication == null || !(CommonsAuthenticationToken.class.isAssignableFrom(authentication.getClass()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((CommonsAuthenticationToken) authentication).getUsername();
        AppUserEntity entity = appUserService.patch(username, AppUserUpdate.builder().setting(setting).build());
        applicationEventPublisher.publishEvent(new UpdateSettingEvent(this, entity));

        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
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
        CommonsAuthenticationToken auth = (CommonsAuthenticationToken) authentication;
        AppUserToken token = appUserTokenService.findByUsername(auth.getUsername()).orElseThrow(NotFoundException::new);
        if (auth.getClientId() != null) {
            Optional<AppClientEntity> optional = appClientService.findById(auth.getClientId());
            if (!optional.isPresent()) {
                log.warn("refresh-token contains invalid clientId: {}", auth.getClientId());
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            token.retainCapabilities(appCapabilityService.resolve(optional.get().getCapabilityIds()));
        }
        applicationEventPublisher.publishEvent(new RefreshTokenEvent(this, token));
        activeUserStore.addUser(token);
        return ResponseEntity.ok(jwtTokenService.generateAccessToken(token));
    }
}
