package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.PasswordChangeRequest;
import io.rocketbase.commons.dto.authentication.UpdateProfileRequest;
import io.rocketbase.commons.event.ChangePasswordEvent;
import io.rocketbase.commons.event.LoginEvent;
import io.rocketbase.commons.event.UpdateProfileEvent;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class AuthenticationController {

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

    @RequestMapping(method = RequestMethod.POST, path = "/auth/login", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JwtTokenBundle> login(@RequestBody @NotNull @Validated LoginRequest login) {
        // Perform the security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUsername().toLowerCase(), login.getPassword())
        );
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        AppUser user = appUserService.updateLastLogin(login.getUsername().toLowerCase());

        applicationEventPublisher.publishEvent(new LoginEvent(this, user));

        return ResponseEntity.ok(jwtTokenService.generateTokenBundle(user.getUsername(), user.getAuthorities()));
    }

    @RequestMapping(value = "/auth/me", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<AppUserRead> getAuthenticated(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(appUserConverter.fromEntity((AppUser) authentication.getPrincipal()));
    }

    @RequestMapping(value = "/auth/change-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@RequestBody @NotNull @Validated PasswordChangeRequest passwordChange, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        // check old password otherwise it throws errors
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, passwordChange.getCurrentPassword())
        );

        appUserService.updatePassword(username, passwordChange.getNewPassword());

        applicationEventPublisher.publishEvent(new ChangePasswordEvent(this, appUserService.getByUsername(username)));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/update-profile", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@RequestBody @NotNull @Validated UpdateProfileRequest updateProfile, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        appUserService.updateProfile(username, updateProfile.getFirstName(), updateProfile.getLastName(), updateProfile.getAvatar(), updateProfile.getKeyValues());

        applicationEventPublisher.publishEvent(new UpdateProfileEvent(this, appUserService.getByUsername(username)));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/refresh", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> refreshToken(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (authentication.getAuthorities() == null || !authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(JwtTokenService.REFRESH_TOKEN))) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        UserDetails user = ((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(jwtTokenService.generateAccessToken(user.getUsername(), user.getAuthorities()));
    }
}
