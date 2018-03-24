package io.rocketbase.commons.controller;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.dto.PasswordChangeRequest;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.resource.AuthenticationResource;
import io.rocketbase.commons.test.AppUserPersistenceTestService;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Resource
    private AuthenticationController authenticationController;

    @Resource
    private AppUserPersistenceTestService appUserPersistenceTestService;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Before
    public void beforeEachTest() {
        appUserPersistenceTestService.resetData();
    }

    @Test
    public void successLogin() {
        // given
        LoginRequest login = LoginRequest.builder()
                .username("user")
                .password("pw")
                .build();

        // when
        ResponseEntity<JwtTokenBundle> response = authenticationController.login(login);

        // then
        JwtTokenBundle jwtTokenBundle = response.getBody();
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
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
            ResponseEntity<JwtTokenBundle> response = authenticationController.login(login);
            // then
            Assert.fail("should have thrown BadRequestException");
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
        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));
        JwtTokenBundle response = resource.login(login);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getRefreshToken(), notNullValue());
        assertThat(response.getToken(), notNullValue());
    }

    @Test
    public void getAuthenticated() {
        // given
        AppUser user = getAppUser();
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);

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
        AppUser user = getAppUser();
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);

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
        AppUser user = getAppUser();
        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        tokenProvider.setRefreshToken("---");
        tokenProvider.setToken(modifiedJwtTokenService.generateExpiredToken(user));

        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            AppUserRead response = resource.getAuthenticated();
            // then
            Assert.fail("should have thrown HttpClientErrorException");
        } catch (HttpClientErrorException e) {
        }
    }

    @Test
    public void refreshToken() throws InterruptedException {
        // given
        AppUser user = getAppUser();
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);
        String token = tokenBundle.getToken();
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);

        // in order to get other token with different time
        TimeUnit.SECONDS.sleep(2);

        // when
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));
        resource.refreshToken();

        // the
        assertThat(token.equals(tokenProvider.getToken()), equalTo(false));
    }

    @Test
    public void changePasswordSuccess() {
        // given
        AppUser user = getAppUser();
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);
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
        AppUser user = getAppUser();
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), tokenBundle);
        AuthenticationResource resource = new AuthenticationResource(new JwtRestTemplate(tokenProvider));

        // when
        try {
            resource.changePassword(PasswordChangeRequest.builder()
                    .currentPassword("wrong-pw")
                    .newPassword("r0cketB@ase")
                    .build());
            // then
            Assert.fail("should have thrown UNAUTHORIZED");
        } catch (HttpClientErrorException e) {
        }
    }

}