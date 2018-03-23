package io.rocketbase.commons.controller;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.resource.AuthenticationResource;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Resource
    private AuthenticationController authenticationController;

    @Resource
    private TestRestTemplate testRestTemplate;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

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
        AppUser user = buildSampleUser();
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
        AppUser user = buildSampleUser();
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
        AppUser user = buildSampleUser();
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

}