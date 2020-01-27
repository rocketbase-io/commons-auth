package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.service.validation.DefaultValidationService;
import io.rocketbase.commons.test.BaseUserIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Set;

import static io.rocketbase.commons.dto.validation.PasswordErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DefaultValidationServiceTest extends BaseUserIntegrationTest {

    @Resource
    private DefaultValidationService service;

    @Test
    public void getPasswordValidationDetailsValid() {
        // given
        String password = "r0cketB@se";
        // when
        Set<ValidationErrorCode<PasswordErrorCodes>> result = service.getPasswordValidationDetails(password);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        assertThat(service.isPasswordValid(password), equalTo(true));
    }

    @Test
    public void getPasswordValidationDetailsInvalid() {
        // given
        String password = "rock";
        // when
        Set<ValidationErrorCode<PasswordErrorCodes>> result = service.getPasswordValidationDetails(password);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(4));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(TOO_SHORT), new ValidationErrorCode<>(INSUFFICIENT_UPPERCASE), new ValidationErrorCode<>(INSUFFICIENT_DIGIT), new ValidationErrorCode<>(INSUFFICIENT_SPECIAL)));
        assertThat(service.isPasswordValid(password), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsValid() {
        // given
        String username = "newuser";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        assertThat(service.isUsernameValid(username), equalTo(true));
    }

    @Test
    public void getUsernameValidationDetailsInvalid() {
        // given
        String username = "kno@wnuer";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(UsernameErrorCodes.NOT_ALLOWED_CHAR)));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooShort() {
        // given
        String username = "kn";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(UsernameErrorCodes.TOO_SHORT)));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooLong() {
        // given
        String username = "kn32188931983210310334dsfkodfdasdas3q4239";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(UsernameErrorCodes.TOO_LONG)));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void validateRegistrationValid() {
        // given
        // when
        boolean result = service.validateRegistration("user123", "r0cketB@se", "test@email.com");
        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void validateRegistrationInvalid() {
        // given
        // when
        try {
            service.validateRegistration("username", "rock", "invalid-email");
        } catch (RegistrationException e) {
            // then
            assertThat(e.getEmailErrors().size(), greaterThan(0));
            assertThat(e.getPasswordErrors().size(), greaterThan(0));
            assertThat(e.getUsernameErrors().size(), equalTo(0));
        }
    }
}