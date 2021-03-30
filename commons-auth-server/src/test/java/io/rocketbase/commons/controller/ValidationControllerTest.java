package io.rocketbase.commons.controller;

import com.google.common.collect.Sets;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.validation.*;
import io.rocketbase.commons.resource.ValidationResource;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.data.CapabilityData;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ValidationControllerTest extends BaseIntegrationTest {

    @Resource
    private AppUserService appUserService;

    @Test
    public void validateUsernameValid() {
        // given
        String username = "username";
        // when
        ValidationResponse<UsernameErrorCodes> response = new ValidationResource(getBaseUrl()).validateUsername(username);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(true));
    }

    @Test
    public void validateUsernameInvalid() {
        // given
        String username = "@e";
        // when
        ValidationResponse<UsernameErrorCodes> response = new ValidationResource(getBaseUrl()).validateUsername(username);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(UsernameErrorCodes.TOO_SHORT, UsernameErrorCodes.NOT_ALLOWED_CHAR));
    }

    @Test
    public void validateEmailValid() {
        // given
        String email = "sample@rocketbase.io";
        // when
        ValidationResponse<EmailErrorCodes> response = new ValidationResource(getBaseUrl()).validateEmail(email);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(true));
    }

    @Test
    public void validateEmailInvalidAlreadyTaken() {
        // given
        String email = "sample@rocketbase.io";
        appUserService.initializeUser(AppUserCreate.builder()
                .username("test-123")
                .password("pw")
                .email(email)
                .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId()))
                .build());

        // when
        ValidationResponse<EmailErrorCodes> response = new ValidationResource(getBaseUrl()).validateEmail(email);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(EmailErrorCodes.ALREADY_TAKEN));
    }

    @Test
    public void validateEmailInvalidFormat() {
        // given
        String email = "sampledrocketbase.io";

        // when
        ValidationResponse<EmailErrorCodes> response = new ValidationResource(getBaseUrl()).validateEmail(email);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(EmailErrorCodes.INVALID));
    }

    @Test
    public void validatePasswordValid() {
        // given
        String password = "r0cketB@se";
        // when
        ValidationResponse<PasswordErrorCodes> response = new ValidationResource(getBaseUrl()).validatePassword(password);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(true));
    }

    @Test
    public void validatePasswordInvalid() {
        // given
        String password = "r0ckets";
        // when
        ValidationResponse<PasswordErrorCodes> response = new ValidationResource(getBaseUrl()).validatePassword(password);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(PasswordErrorCodes.INSUFFICIENT_SPECIAL, PasswordErrorCodes.INSUFFICIENT_UPPERCASE, PasswordErrorCodes.TOO_SHORT));
    }

    @Test
    public void validateToken() {
        // given
        String token = SimpleTokenService.generateToken("test", 10);
        // when
        ValidationResponse<TokenErrorCodes> response = new ValidationResource(getBaseUrl()).validateToken(token);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(true));
    }

    @Test
    public void validateTokenExpired() {
        // given
        String token = SimpleTokenService.generateToken("test", -1L);
        // when
        ValidationResponse<TokenErrorCodes> response = new ValidationResource(getBaseUrl()).validateToken(token);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(TokenErrorCodes.EXPIRED));
    }

    @Test
    public void validateTokenInvalid() {
        // given
        String token = "xyz";
        // when
        ValidationResponse<TokenErrorCodes> response = new ValidationResource(getBaseUrl()).validateToken(token);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes().keySet(), containsInAnyOrder(TokenErrorCodes.INVALID));
    }

}