package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.config.PasswordProperties;
import io.rocketbase.commons.config.UsernameProperties;
import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.passay.SpecialCharacterRule;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class ValidationErrorCodeService {

    final UsernameProperties usernameProperties;
    final PasswordProperties passwordProperties;
    final MessageSource messageSource;

    public ValidationErrorCode<PasswordErrorCodes> passwordError(PasswordErrorCodes error) {
        switch (error) {
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
                return generateError(error, e -> e.getValue());
        }
    }

    public ValidationErrorCode<UsernameErrorCodes> usernameError(UsernameErrorCodes error) {
        switch (error) {
            case TOO_SHORT:
                return generateError(UsernameErrorCodes.TOO_SHORT, e -> e.getValue(), usernameProperties.getMinLength());
            case TOO_LONG:
                return generateError(UsernameErrorCodes.TOO_LONG, e -> e.getValue(), usernameProperties.getMaxLength());
            case NOT_ALLOWED_CHAR:
                return generateError(UsernameErrorCodes.NOT_ALLOWED_CHAR, e -> e.getValue(), String.format("a-z, 0-9 and %s", usernameProperties.getSpecialCharacters()));
            default:
                return generateError(error, e -> e.getValue());
        }
    }

    public ValidationErrorCode<EmailErrorCodes> emailError(EmailErrorCodes error) {
        switch (error) {
            case ALREADY_TAKEN:
                return generateError(EmailErrorCodes.ALREADY_TAKEN, e -> e.getValue(), usernameProperties.getMinLength());
            case TOO_LONG:
                return generateError(EmailErrorCodes.TOO_LONG, e -> e.getValue(), ValidationService.EMAIL_MAX_LENGTH);
            default:
                return generateError(error, e -> e.getValue());
        }
    }

    public Set<ValidationErrorCode<PasswordErrorCodes>> passwordError(PasswordErrorCodes... errors) {
        Set<ValidationErrorCode<PasswordErrorCodes>> result = new HashSet<>();
        for (PasswordErrorCodes e : Nulls.notNull(errors, new PasswordErrorCodes[]{})) {
            result.add(passwordError(e));
        }
        return result;
    }

    public Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrors(UsernameErrorCodes... errors) {
        Set<ValidationErrorCode<UsernameErrorCodes>> result = new HashSet<>();
        for (UsernameErrorCodes e : Nulls.notNull(errors, new UsernameErrorCodes[]{})) {
            result.add(usernameError(e));
        }
        return result;
    }

    public Set<ValidationErrorCode<EmailErrorCodes>> emailErrors(EmailErrorCodes... errors) {
        Set<ValidationErrorCode<EmailErrorCodes>> result = new HashSet<>();
        for (EmailErrorCodes e : Nulls.notNull(errors, new EmailErrorCodes[]{})) {
            result.add(emailError(e));
        }
        return result;
    }


    private <T extends Enum<T>> ValidationErrorCode<T> generateError(T error, Function<T, String> errorCode, Object... args) {
        return new ValidationErrorCode<T>(error, messageSource.getMessage(String.format("auth.error.%s", errorCode.apply(error)), args, LocaleContextHolder.getLocale()));
    }
}
