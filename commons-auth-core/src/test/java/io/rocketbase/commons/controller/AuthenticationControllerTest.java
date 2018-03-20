package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;


public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Resource
    private AuthenticationController authenticationController;


    @Resource
    private JwtTokenService jwtTokenService;

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
}