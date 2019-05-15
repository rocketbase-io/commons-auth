package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.dto.validation.ValidationResponse;
import io.rocketbase.commons.resource.ValidationResource;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Test;

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
        assertThat(response.getErrorCodes(), containsInAnyOrder(UsernameErrorCodes.TOO_SHORT, UsernameErrorCodes.NOT_ALLOWED_CHAR));
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
        appUserService.initializeUser("test-123", "pw", email, false);

        // when
        ValidationResponse<EmailErrorCodes> response = new ValidationResource(getBaseUrl()).validateEmail(email);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isValid(), equalTo(false));
        assertThat(response.getErrorCodes(), containsInAnyOrder(EmailErrorCodes.ALREADY_TAKEN));
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
        assertThat(response.getErrorCodes(), containsInAnyOrder(EmailErrorCodes.INVALID));
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
        assertThat(response.getErrorCodes(), containsInAnyOrder(PasswordErrorCodes.INSUFFICIENT_SPECIAL, PasswordErrorCodes.INSUFFICIENT_UPPERCASE, PasswordErrorCodes.TOO_SHORT));
    }

}