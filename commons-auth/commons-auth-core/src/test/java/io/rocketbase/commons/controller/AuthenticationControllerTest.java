package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.LoginRequest;
import io.rocketbase.commons.service.AppUserService;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;


public class AuthenticationControllerTest extends BaseIntegrationTest {

    @Resource
    private AuthenticationController authenticationController;

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
}