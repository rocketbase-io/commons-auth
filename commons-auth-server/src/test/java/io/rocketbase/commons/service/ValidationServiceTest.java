package io.rocketbase.commons.service;

import io.rocketbase.commons.config.PasswordProperties;
import io.rocketbase.commons.config.UsernameProperties;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static io.rocketbase.commons.dto.validation.PasswordErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ValidationServiceTest {


    private final ValidationUserLookupService takenUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUser getByUsername(String username) {
            return AppUserTestEntity.builder().username(username).build();
        }

        @Override
        public Optional<AppUser> findByEmail(String email) {
            return Optional.empty();
        }
    };
    private final ValidationUserLookupService unkownUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUser getByUsername(String username) {
            return null;
        }

        @Override
        public Optional<AppUser> findByEmail(String email) {
            return Optional.empty();
        }
    };
    private final ValidationUserLookupService knownEmailUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUser getByUsername(String username) {
            return null;
        }

        @Override
        public Optional<AppUser> findByEmail(String email) {
            return Optional.of(AppUserTestEntity.builder().username("found").build());
        }
    };

    @Test
    public void getPasswordValidationDetailsValid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService);
        String password = "r0cketB@se";
        // when
        Set<PasswordErrorCodes> result = service.getPasswordValidationDetails(password);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        assertThat(service.isPasswordValid(password), equalTo(true));
    }

    @Test
    public void getPasswordValidationDetailsInvalid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService);
        String password = "rock";
        // when
        Set<PasswordErrorCodes> result = service.getPasswordValidationDetails(password);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(4));
        assertThat(result, containsInAnyOrder(TOO_SHORT, INSUFFICIENT_UPPERCASE, INSUFFICIENT_DIGIT, INSUFFICIENT_SPECIAL));
        assertThat(service.isPasswordValid(password), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsValid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService);
        String username = "newuser";
        // when
        Set<UsernameErrorCodes> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        assertThat(service.isUsernameValid(username), equalTo(true));
    }

    @Test
    public void getUsernameValidationDetailsInvalid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), takenUserLookupService);
        String username = "kno@wnuer";
        // when
        Set<UsernameErrorCodes> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(UsernameErrorCodes.ALREADY_TAKEN, UsernameErrorCodes.NOT_ALLOWED_CHAR));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooShort() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), takenUserLookupService);
        String username = "kn";
        // when
        Set<UsernameErrorCodes> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(UsernameErrorCodes.TOO_SHORT, UsernameErrorCodes.ALREADY_TAKEN));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooLong() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService);
        String username = "kn32188931983210310334dsfkodfdasdas3q4239";
        // when
        Set<UsernameErrorCodes> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result, containsInAnyOrder(UsernameErrorCodes.TOO_LONG));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void validateRegistrationValid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService);
        // when
        boolean result = service.validateRegistration("user123", "r0cketB@se", "test@email.com");
        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void validateRegistrationInvalid() {
        // given
        ValidationService service = new ValidationService(new UsernameProperties(), new PasswordProperties(), knownEmailUserLookupService);
        // when
        try {
            service.validateRegistration("user123", "rock", "test@email.com");
        } catch (RegistrationException e) {
            // then
            assertThat(e.getEmailErrors().size(), greaterThan(0));
            assertThat(e.getPasswordErrors().size(), greaterThan(0));
            assertThat(e.getUsernameErrors().size(), equalTo(0));
        }
    }
}