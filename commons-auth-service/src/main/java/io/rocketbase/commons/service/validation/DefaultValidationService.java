package io.rocketbase.commons.service.validation;


import io.rocketbase.commons.config.PasswordProperties;
import io.rocketbase.commons.config.UsernameProperties;
import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.*;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.service.ValidationUserLookupService;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultValidationService implements io.rocketbase.commons.service.validation.ValidationService {

    public static final int EMAIL_MAX_LENGTH = 255;

    final UsernameProperties usernameProperties;
    final PasswordProperties passwordProperties;
    final ValidationUserLookupService userLookupService;
    final MessageSource messageSource;

    private Pattern userMatcher;
    private PasswordValidator passwordValidator;

    protected PasswordValidator getPasswordValidator() {
        if (passwordValidator == null) {
            List<Rule> rules = new ArrayList<>();
            rules.add(new LengthRule(passwordProperties.getMinLength(), passwordProperties.getMaxLength()));
            if (passwordProperties.getLowercase() > 0) {
                rules.add(new LowercaseCharacterRule(passwordProperties.getLowercase()));
            }
            if (passwordProperties.getUppercase() > 0) {
                rules.add(new UppercaseCharacterRule(passwordProperties.getUppercase()));
            }
            if (passwordProperties.getDigit() > 0) {
                rules.add(new DigitCharacterRule(passwordProperties.getDigit()));
            }
            if (passwordProperties.getSpecial() > 0) {
                rules.add(new SpecialCharacterRule(passwordProperties.getSpecial()));
            }
            passwordValidator = new PasswordValidator(rules);
        }
        return passwordValidator;
    }

    protected Pattern getUserMatcher() {
        if (userMatcher == null) {
            StringBuffer patternStr = new StringBuffer();
            patternStr.append("^[a-z0-9");
            String specialCharacters = usernameProperties.getSpecialCharacters();
            for (int x = 0; x < specialCharacters.length(); x++) {
                patternStr.append("\\").append(specialCharacters.charAt(x));
            }
            patternStr.append("]+$");

            userMatcher = Pattern.compile(patternStr.toString());
        }
        return userMatcher;
    }

    protected RuleResult runPasswordValidation(String password) {
        return getPasswordValidator()
                .validate(new PasswordData(Nulls.notNull(password)));
    }

    @Override
    public boolean isPasswordValid(String password) {
        return runPasswordValidation(password).isValid();
    }

    @Override
    public void passwordIsValid(String password) throws PasswordValidationException {
        Set<ValidationErrorCode<PasswordErrorCodes>> errorCodes = getPasswordValidationDetails(password);
        if (!errorCodes.isEmpty()) {
            PasswordValidationException exception = new PasswordValidationException(errorCodes);
            if (log.isDebugEnabled()) {
                log.debug("passwordIsValid(***): {}", exception.toString());
            }
            throw exception;
        }
    }

    @Override
    public Set<ValidationErrorCode<PasswordErrorCodes>> getPasswordValidationDetails(String password) {
        return runPasswordValidation(password).getDetails().stream()
                .map(d -> {
                    PasswordErrorCodes errorCode = PasswordErrorCodes.valueOf(d.getErrorCode());
                    switch (errorCode) {
                        case TOO_SHORT:
                            return generateError(PasswordErrorCodes.TOO_SHORT, e -> e.getValue(), passwordProperties.getMinLength());
                        case TOO_LONG:
                            return generateError(PasswordErrorCodes.TOO_LONG, e -> e.getValue(), passwordProperties.getMaxLength());
                        case INSUFFICIENT_LOWERCASE:
                            return generateError(PasswordErrorCodes.INSUFFICIENT_LOWERCASE, e -> e.getValue(), passwordProperties.getLowercase());
                        case INSUFFICIENT_UPPERCASE:
                            return generateError(PasswordErrorCodes.INSUFFICIENT_UPPERCASE, e -> e.getValue(), passwordProperties.getUppercase());
                        case INSUFFICIENT_DIGIT:
                            return generateError(PasswordErrorCodes.INSUFFICIENT_DIGIT, e -> e.getValue(), passwordProperties.getDigit());
                        case INSUFFICIENT_SPECIAL:
                            return generateError(PasswordErrorCodes.INSUFFICIENT_SPECIAL, e -> e.getValue(), SpecialCharacterRule.CHARS, passwordProperties.getSpecial());
                        default:
                            return generateError(errorCode, e -> e.getValue());
                    }
                })
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isUsernameValid(String username) {
        return getUsernameValidationDetails(username).isEmpty();
    }

    @Override
    public void usernameIsValid(String username) throws UsernameValidationException {
        Set<ValidationErrorCode<UsernameErrorCodes>> errorCodes = getUsernameValidationDetails(username);
        if (!errorCodes.isEmpty()) {
            UsernameValidationException exception = new UsernameValidationException(errorCodes);
            if (log.isDebugEnabled()) {
                log.debug("usernameIsValid({}): {}", username, exception.toString());
            }
            throw exception;
        }
    }

    @Override
    public Set<ValidationErrorCode<UsernameErrorCodes>> getUsernameValidationDetails(String username) {
        Set<ValidationErrorCode<UsernameErrorCodes>> errorCodes = new HashSet<>();
        if (StringUtils.isEmpty(username)) {
            errorCodes.add(generateError(UsernameErrorCodes.TOO_SHORT, e -> e.getValue(), usernameProperties.getMinLength()));
            return errorCodes;
        }
        if (username.length() < usernameProperties.getMinLength()) {
            errorCodes.add(generateError(UsernameErrorCodes.TOO_SHORT, e -> e.getValue(), usernameProperties.getMinLength()));
        }
        if (username.length() > usernameProperties.getMaxLength()) {
            errorCodes.add(generateError(UsernameErrorCodes.TOO_LONG, e -> e.getValue(), usernameProperties.getMinLength()));
        }
        if (!getUserMatcher().matcher(username).matches()) {
            errorCodes.add(generateError(UsernameErrorCodes.NOT_ALLOWED_CHAR, e -> e.getValue(), String.format("a-z, 0-9 and %s", usernameProperties.getSpecialCharacters())));
        }
        AppUserToken found = userLookupService.getByUsername(username);
        if (found != null) {
            errorCodes.add(generateError(UsernameErrorCodes.ALREADY_TAKEN, e -> e.getValue()));
        }
        return errorCodes;
    }

    @Override
    public boolean isEmailValid(String email) {
        return getEmailValidationDetails(email).isEmpty();
    }

    @Override
    public Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String email) {
        Set<ValidationErrorCode<EmailErrorCodes>> errorCodes = new HashSet<>();
        if (StringUtils.isEmpty(email)) {
            errorCodes.add(generateError(EmailErrorCodes.INVALID, e -> e.getValue()));
            return errorCodes;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            errorCodes.add(generateError(EmailErrorCodes.INVALID, e -> e.getValue()));
        }
        if (userLookupService.findByEmail(email).isPresent()) {
            errorCodes.add(generateError(EmailErrorCodes.ALREADY_TAKEN, e -> e.getValue()));
        }
        // max length within jpa entity
        if (email.length() > EMAIL_MAX_LENGTH) {
            errorCodes.add(generateError(EmailErrorCodes.TOO_LONG, e -> e.getValue(), EMAIL_MAX_LENGTH));
        }
        return errorCodes;
    }

    @Override
    public void emailIsValid(String email) {
        Set<ValidationErrorCode<EmailErrorCodes>> errorCodes = getEmailValidationDetails(email);
        if (!errorCodes.isEmpty()) {
            EmailValidationException exception = new EmailValidationException(errorCodes);
            if (log.isDebugEnabled()) {
                log.debug("emailIsValid({}): {}", email, exception.toString());
            }
            throw exception;
        }
    }

    @Override
    public boolean validateRegistration(String username, String password, String email) {
        Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrorCodes = getUsernameValidationDetails(username);
        Set<ValidationErrorCode<PasswordErrorCodes>> passwordErrorCodes = getPasswordValidationDetails(password);
        Set<ValidationErrorCode<EmailErrorCodes>> emailErrorCodes = getEmailValidationDetails(email);

        if (!usernameErrorCodes.isEmpty() || !passwordErrorCodes.isEmpty() || !emailErrorCodes.isEmpty()) {
            RegistrationException exception = new RegistrationException(usernameErrorCodes, passwordErrorCodes, emailErrorCodes);
            if (log.isDebugEnabled()) {
                log.debug("validateRegistration(username: {}, password: {}, email: {}): {}", username, password, email, exception.toString());
            }
            throw exception;
        }
        return true;
    }

    private <T extends Enum<T>> ValidationErrorCode<T> generateError(T error, Function<T, String> errorCode, Object... args) {
        return new ValidationErrorCode<T>(error, messageSource.getMessage(String.format("auth.error.%s", errorCode.apply(error)), args, LocaleContextHolder.getLocale()));
    }
}
