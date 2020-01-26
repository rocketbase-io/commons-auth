package io.rocketbase.commons.service;

import io.rocketbase.commons.config.PasswordProperties;
import io.rocketbase.commons.config.UsernameProperties;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.validation.DefaultValidationService;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static io.rocketbase.commons.dto.validation.PasswordErrorCodes.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DefaultValidationServiceTest {


    private final ValidationUserLookupService takenUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUserEntity getByUsername(String username) {
            return AppUserTestEntity.builder().username(username).build();
        }

        @Override
        public Optional<AppUserEntity> findByEmail(String email) {
            return Optional.empty();
        }
    };
    private final ValidationUserLookupService unkownUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUserEntity getByUsername(String username) {
            return null;
        }

        @Override
        public Optional<AppUserEntity> findByEmail(String email) {
            return Optional.empty();
        }
    };
    private final ValidationUserLookupService knownEmailUserLookupService = new ValidationUserLookupService() {
        @Override
        public AppUserEntity getByUsername(String username) {
            return null;
        }

        @Override
        public Optional<AppUserEntity> findByEmail(String email) {
            return Optional.of(AppUserTestEntity.builder().username("found").build());
        }
    };
    private final MessageSource messageSource = new MessageSource() {
        @Override
        public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
            return code;
        }

        @Override
        public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
            return code;
        }

        @Override
        public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
            return "";
        }
    };

    @Test
    public void getPasswordValidationDetailsValid() {
        // given
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService, messageSource);
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
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService, messageSource);
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
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService, messageSource);
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
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), takenUserLookupService, messageSource);
        String username = "kno@wnuer";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(UsernameErrorCodes.ALREADY_TAKEN), new ValidationErrorCode<>(UsernameErrorCodes.NOT_ALLOWED_CHAR)));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooShort() {
        // given
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), takenUserLookupService, messageSource);
        String username = "kn";
        // when
        Set<ValidationErrorCode<UsernameErrorCodes>> result = service.getUsernameValidationDetails(username);
        // then
        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result, containsInAnyOrder(new ValidationErrorCode<>(UsernameErrorCodes.TOO_SHORT), new ValidationErrorCode<>(UsernameErrorCodes.ALREADY_TAKEN)));
        assertThat(service.isUsernameValid(username), equalTo(false));
    }

    @Test
    public void getUsernameValidationDetailsTooLong() {
        // given
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService, messageSource);
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
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), unkownUserLookupService, messageSource);
        // when
        boolean result = service.validateRegistration("user123", "r0cketB@se", "test@email.com");
        // then
        assertThat(result, equalTo(true));
    }

    @Test
    public void validateRegistrationInvalid() {
        // given
        DefaultValidationService service = new DefaultValidationService(new UsernameProperties(), new PasswordProperties(), knownEmailUserLookupService, messageSource);
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