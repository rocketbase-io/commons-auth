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
import org.springframework.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultValidationService implements io.rocketbase.commons.service.validation.ValidationService {

    final UsernameProperties usernameProperties;
    final PasswordProperties passwordProperties;
    final ValidationUserLookupService userLookupService;
    final ValidationErrorCodeService validationErrorCodeService;

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
                    return validationErrorCodeService.passwordError(errorCode);
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
        Set<UsernameErrorCodes> errorCodes = new HashSet<>();
        if (StringUtils.isEmpty(username)) {
            errorCodes.add(UsernameErrorCodes.TOO_SHORT);
        } else {
            if (username.length() < usernameProperties.getMinLength()) {
                errorCodes.add(UsernameErrorCodes.TOO_SHORT);
            }
            if (username.length() > usernameProperties.getMaxLength()) {
                errorCodes.add(UsernameErrorCodes.TOO_LONG);
            }
            if (!getUserMatcher().matcher(username).matches()) {
                errorCodes.add(UsernameErrorCodes.NOT_ALLOWED_CHAR);
            }
            AppUserToken found = userLookupService.getByUsername(username);
            if (found != null) {
                errorCodes.add(UsernameErrorCodes.ALREADY_TAKEN);
            }
        }
        return validationErrorCodeService.usernameErrors(errorCodes.toArray(new UsernameErrorCodes[]{}));
    }

    @Override
    public boolean isEmailValid(String email) {
        return getEmailValidationDetails(email).isEmpty();
    }

    @Override
    public Set<ValidationErrorCode<EmailErrorCodes>> getEmailValidationDetails(String email) {
        Set<EmailErrorCodes> errorCodes = new HashSet<>();
        if (StringUtils.isEmpty(email)) {
            errorCodes.add(EmailErrorCodes.INVALID);
        } else {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
            } catch (AddressException ex) {
                errorCodes.add(EmailErrorCodes.INVALID);
            }
            if (userLookupService.findByEmail(email).isPresent()) {
                errorCodes.add(EmailErrorCodes.ALREADY_TAKEN);
            }
            // max length within jpa entity
            if (email.length() > EMAIL_MAX_LENGTH) {
                errorCodes.add(EmailErrorCodes.TOO_LONG);
            }
        }
        return validationErrorCodeService.emailErrors(errorCodes.toArray(new EmailErrorCodes[]{}));
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
}
