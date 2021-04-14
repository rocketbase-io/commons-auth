package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.*;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.resource.AuthenticationResource;
import io.rocketbase.commons.resource.BasicResponseErrorHandler;
import io.rocketbase.commons.resource.LoginResource;
import io.rocketbase.commons.service.change.DefaultChangeAppUserWithConfirmService;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Resource
    private AuthenticationController authenticationController;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Resource
    private AppUserPersistenceService<AppUserEntity> appUserPersistenceService;

    @Test
    public void successLogin() {
        // given
        LoginRequest login = LoginRequest.builder()
                .username("user")
                .password("pw")
                .build();

        // when
        ResponseEntity<LoginResponse> response = authenticationController.login(login);

        // then
        LoginResponse loginResponse = response.getBody();
        assertThat(loginResponse, notNullValue());
        assertThat(loginResponse.getJwtTokenBundle().getRefreshToken(), notNullValue());
        assertThat(loginResponse.getJwtTokenBundle().getToken(), notNullValue());
    }

    @Test
    public void successLoginViaEmail() {
        // given
        LoginRequest login = LoginRequest.builder()
                .username(getAppUser("user").getEmail())
                .password("pw")
                .build();

        // when
        ResponseEntity<LoginResponse> response = authenticationController.login(login);

        // then
        LoginResponse loginResponse = response.getBody();
        assertThat(loginResponse, notNullValue());
        assertThat(loginResponse.getJwtTokenBundle().getRefreshToken(), notNullValue());
        assertThat(loginResponse.getJwtTokenBundle().getToken(), notNullValue());
    }

    @Test
    public void invalidLogin() {
        // given
        LoginRequest login = LoginRequest.builder()
                .username("user")
                .password("--")
                .build();

        // when
        try {
            ResponseEntity<LoginResponse> response = authenticationController.login(login);
            // then
            Assertions.fail("should have thrown BadRequestException");
        } catch (Exception e) {
        }
    }

    @Test
    public void restResourceLogin() {
        // given
        LoginRequest login = LoginRequest.builder()
                .username("user")
                .password("pw")
                .build();

        // when
        LoginResource resource = new LoginResource(getBaseUrl());
        LoginResponse response = resource.login(login);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getJwtTokenBundle().getRefreshToken(), notNullValue());
        assertThat(response.getJwtTokenBundle().getToken(), notNullValue());
    }

    @Test
    public void getAuthenticated() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));

        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);

        // when
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = resource.getAuthenticated();

        // the
        assertThat(response, notNullValue());
        assertThat(response.getUsername(), equalTo(user.getUsername()));
        assertThat(response.getEmail(), equalTo(user.getEmail()));
        assertThat(tokenProvider.getToken(), equalTo(tokenBundle.getToken()));
    }

    @Test
    public void getAuthenticatedUserRefreshToken() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));

        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        tokenProvider.setRefreshToken(tokenBundle.getRefreshToken());
        String expiredToken = modifiedJwtTokenService.generateExpiredToken(user);
        tokenProvider.setToken(expiredToken);

        // when
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = resource.getAuthenticated();

        // the
        assertThat(response, notNullValue());
        assertThat(response.getUsername(), equalTo(user.getUsername()));
        assertThat(response.getEmail(), equalTo(user.getEmail()));
        assertThat(tokenProvider.getToken().equals(expiredToken), equalTo(false));
    }

    @Test
    public void getAuthenticatedWithInvalidRefreshToken() {
        // given
        AppUserEntity user = getAppUser("user");
        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        tokenProvider.setRefreshToken("---");
        tokenProvider.setToken(modifiedJwtTokenService.generateExpiredToken(user));

        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            AppUserRead response = resource.getAuthenticated();
            // then
            Assertions.fail("should have thrown HttpClientErrorException");
        } catch (HttpClientErrorException e) {
        }
    }

    @Test
    public void refreshToken() throws InterruptedException {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        String token = tokenBundle.getToken();
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);

        // in order to get other token with different time
        TimeUnit.SECONDS.sleep(2);

        // when
        LoginResource resource = new LoginResource(getBaseUrl());
        resource.refreshAccessToken(tokenProvider);

        // the
        assertThat(token.equals(tokenProvider.getToken()), equalTo(false));
    }

    @Test
    public void changePasswordSuccess() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        resource.changePassword(PasswordChangeRequest.builder()
                .currentPassword("pw")
                .newPassword("r0cketB@ase")
                .build());
    }

    @Test
    public void changePasswordFailure() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            resource.changePassword(PasswordChangeRequest.builder()
                    .currentPassword("wrong-pw")
                    .newPassword("r0cketB@ase")
                    .build());
            // then
            Assertions.fail("should have thrown PasswordValidationException");
        } catch (BadRequestException e) {
            assertThat(e.getErrorResponse(), notNullValue());
            assertThat(e.getErrorResponse().getFields(), notNullValue());
            assertThat(e.getErrorResponse().hasField("currentPassword"), equalTo(true));
        }
    }


    @Test
    public void updateProfile() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        String avatar = "https://www.gravatar.com/avatar/fc40e22b7bcd7230b49c34b113d5dbc.jpg?s=160&d=retro";
        resource.updateProfile(SimpleUserProfile.builder()
                .firstName("firstName")
                .lastName("lastName")
                .avatar(avatar)
                .build());

        AppUserRead response = resource.getAuthenticated();

        // then
        assertThat(response.getFirstName(), equalTo("firstName"));
        assertThat(response.getLastName(), equalTo("lastName"));
        assertThat(response.getAvatar(), equalTo(avatar));

    }


    @Test
    public void changeUsername() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        String newUsername = "halli.galli";
        AppUserRead appUserRead = resource.changeUsername(new UsernameChangeRequest(newUsername));

        // then
        assertThat(appUserRead.getUsername(), equalTo(newUsername));
        assertThat(appUserPersistenceService.findById(appUserRead.getId()).get().getUsername(), equalTo(newUsername));
    }

    @Test
    public void changeUsernameUsed() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            resource.changeUsername(new UsernameChangeRequest("admin"));
            throw new RuntimeException("should get error");
        } catch (BadRequestException badRequestException) {
            assertThat(badRequestException.getMessage(), equalTo("Username not fitting requirements"));
            assertThat(badRequestException.getErrorResponse().getFirstFieldValue("newUsername"), notNullValue());
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    public void changeEmailAddress() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        String newEmail = "new-email@rocketbase.io";
        ExpirationInfo<AppUserRead> expirationInfo = resource.changeEmail(new EmailChangeRequest(newEmail));

        // then
        assertThat(expirationInfo.isExpired(), equalTo(false));
        assertThat(expirationInfo.getExpires(), notNullValue());
        AppUserEntity appUser = appUserPersistenceService.findById(expirationInfo.getDetail().getId()).get();
        assertThat(appUser.getKeyValue(DefaultChangeAppUserWithConfirmService.CHANGEMAIL_VALUE), equalTo(newEmail));
        assertThat(appUser.getEmail(), equalTo(user.getEmail()));

        String token = appUser.getKeyValue(DefaultChangeAppUserWithConfirmService.CHANGEMAIL_TOKEN);
        AppUserRead appUserRead = new AuthenticationResource(getBaseUrl(), new RestTemplate()).verifyEmail(token);
        assertThat(appUserRead.getEmail(), equalTo(newEmail));
        assertThat(appUserPersistenceService.findById(expirationInfo.getDetail().getId()).get().getEmail(), equalTo(newEmail));
    }

    @Test
    public void changeEmailAddressUsed() {
        // given
        AppUserEntity user = getAppUser("user");
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(toToken(user));
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            resource.changeEmail(new EmailChangeRequest("admin@rocketbase.io"));
            throw new RuntimeException("should get error");
        } catch (BadRequestException badRequestException) {
            assertThat(badRequestException.getMessage(), equalTo("Email is used or incorrect"));
            assertThat(badRequestException.getErrorResponse().getFirstFieldValue("newEmail"), notNullValue());
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    public void changeEmailAddressInvalidVerification() {
        // given
        String invalidToken = "invalidABC";
        // when
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new BasicResponseErrorHandler());
            new AuthenticationResource(getBaseUrl(), restTemplate)
                    .verifyEmail(invalidToken);
            throw new RuntimeException("should get error");
        } catch (BadRequestException badRequestException) {
            assertThat(badRequestException.getMessage(), equalTo("Verification is invalid or expired"));
            assertThat(badRequestException.getErrorResponse().getFirstFieldValue("verification"), notNullValue());
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

}