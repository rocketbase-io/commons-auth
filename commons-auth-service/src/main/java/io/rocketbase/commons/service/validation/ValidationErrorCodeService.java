package io.rocketbase.commons.service.validation;

import io.rocketbase.commons.config.PasswordProperties;
import io.rocketbase.commons.config.UsernameProperties;
import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.TokenErrorCodes;
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

    public ValidationErrorCode<PasswordErrorCodes> passwordError(String field, PasswordErrorCodes error) {
        switch (error) {
            case TOO_SHORT:
                return generateError(PasswordErrorCodes.TOO_SHORT, e -> e.getValue(), field, passwordProperties.getMinLength());
            case TOO_LONG:
                return generateError(PasswordErrorCodes.TOO_LONG, e -> e.getValue(), field, passwordProperties.getMaxLength());
            case INSUFFICIENT_LOWERCASE:
                return generateError(PasswordErrorCodes.INSUFFICIENT_LOWERCASE, e -> e.getValue(), field, passwordProperties.getLowercase());
            case INSUFFICIENT_UPPERCASE:
                return generateError(PasswordErrorCodes.INSUFFICIENT_UPPERCASE, e -> e.getValue(), field, passwordProperties.getUppercase());
            case INSUFFICIENT_DIGIT:
                return generateError(PasswordErrorCodes.INSUFFICIENT_DIGIT, e -> e.getValue(), field, passwordProperties.getDigit());
            case INSUFFICIENT_SPECIAL:
                return generateError(PasswordErrorCodes.INSUFFICIENT_SPECIAL, e -> e.getValue(), field, SpecialCharacterRule.CHARS, passwordProperties.getSpecial());
            default:
                return generateError(error, e -> e.getValue(), field);
        }
    }

    public ValidationErrorCode<UsernameErrorCodes> usernameError(String field, UsernameErrorCodes error) {
        switch (error) {
            case TOO_SHORT:
                return generateError(UsernameErrorCodes.TOO_SHORT, e -> e.getValue(), field, usernameProperties.getMinLength());
            case TOO_LONG:
                return generateError(UsernameErrorCodes.TOO_LONG, e -> e.getValue(), field, usernameProperties.getMaxLength());
            case NOT_ALLOWED_CHAR:
                return generateError(UsernameErrorCodes.NOT_ALLOWED_CHAR, e -> e.getValue(), field, String.format("a-z, 0-9 & \"%s\"", usernameProperties.getSpecialCharacters()));
            default:
                return generateError(error, e -> e.getValue(), field);
        }
    }

    public ValidationErrorCode<EmailErrorCodes> emailError(String field, EmailErrorCodes error) {
        switch (error) {
            case ALREADY_TAKEN:
                return generateError(EmailErrorCodes.ALREADY_TAKEN, e -> e.getValue(), field, usernameProperties.getMinLength());
            case TOO_LONG:
                return generateError(EmailErrorCodes.TOO_LONG, e -> e.getValue(), field, ValidationService.EMAIL_MAX_LENGTH);
            default:
                return generateError(error, e -> e.getValue(), field);
        }
    }

    public ValidationErrorCode<TokenErrorCodes> tokenError(String field, TokenErrorCodes error) {
        return generateError(error, e -> e.getValue(), field);
    }

    public Set<ValidationErrorCode<PasswordErrorCodes>> passwordError(String field, PasswordErrorCodes... errors) {
        Set<ValidationErrorCode<PasswordErrorCodes>> result = new HashSet<>();
        for (PasswordErrorCodes e : Nulls.notNull(errors, new PasswordErrorCodes[]{})) {
            result.add(passwordError(field, e));
        }
        return result;
    }

    public Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrors(String field, UsernameErrorCodes... errors) {
        Set<ValidationErrorCode<UsernameErrorCodes>> result = new HashSet<>();
        for (UsernameErrorCodes e : Nulls.notNull(errors, new UsernameErrorCodes[]{})) {
            result.add(usernameError(field, e));
        }
        return result;
    }

    public Set<ValidationErrorCode<EmailErrorCodes>> emailErrors(String field, EmailErrorCodes... errors) {
        Set<ValidationErrorCode<EmailErrorCodes>> result = new HashSet<>();
        for (EmailErrorCodes e : Nulls.notNull(errors, new EmailErrorCodes[]{})) {
            result.add(emailError(field, e));
        }
        return result;
    }

    public Set<ValidationErrorCode<TokenErrorCodes>> tokenErrors(String field, TokenErrorCodes... errors) {
        Set<ValidationErrorCode<TokenErrorCodes>> result = new HashSet<>();
        for (TokenErrorCodes e : Nulls.notNull(errors, new TokenErrorCodes[]{})) {
            result.add(tokenError(field, e));
        }
        return result;
    }

    private <T extends Enum<T>> ValidationErrorCode<T> generateError(T error, Function<T, String> errorCode, String field, Object... args) {
        String code = String.format("auth.error.%s", errorCode.apply(error));
        return new ValidationErrorCode<T>(error, field, messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale()));
    }
}
