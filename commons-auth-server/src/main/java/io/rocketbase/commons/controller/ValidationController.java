package io.rocketbase.commons.controller;


import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.validation.*;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
@RequiredArgsConstructor
public class ValidationController implements BaseController {

    private final MessageSource messageSource;

    @Resource
    private ValidationService validationService;


    @RequestMapping(value = "/auth/validate/password", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<PasswordErrorCodes>> validatePassword(@RequestBody String password) {
        Set<ValidationErrorCode<PasswordErrorCodes>> passwordErrorCodes = validationService.getPasswordValidationDetails(password);
        return ResponseEntity.ok(convert(passwordErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/username", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<UsernameErrorCodes>> validateUsername(@RequestBody String username) {
        Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrorCodes = validationService.getUsernameValidationDetails(username);
        return ResponseEntity.ok(convert(usernameErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/email", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<EmailErrorCodes>> validateEmail(@RequestBody String email) {
        Set<ValidationErrorCode<EmailErrorCodes>> emailErrorCodes = validationService.getEmailValidationDetails(email);
        return ResponseEntity.ok(convert(emailErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/token", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<TokenErrorCodes>> validateToken(@RequestBody String token) {
        boolean valid = SimpleTokenService.parseToken(token).isValid();
        ValidationResponse<TokenErrorCodes> response = new ValidationResponse<>(valid, null);
        if (!valid) {
            response.setErrorCodes(ImmutableMap.of(TokenErrorCodes.EXPIRED, messageSource.getMessage("auth.error." + TokenErrorCodes.EXPIRED.getValue(), null, TokenErrorCodes.EXPIRED.getValue(), LocaleContextHolder.getLocale())));
        }
        return ResponseEntity.ok(response);
    }


    private <T extends Enum<T>> ValidationResponse<T> convert(Set<ValidationErrorCode<T>> errors) {
        ValidationResponse<T> response = new ValidationResponse<>(errors != null && errors.isEmpty(), new HashMap<>());
        if (errors != null) {
            for (ValidationErrorCode<T> c : errors) {
                response.getErrorCodes().put(c.getCode(), c.getMessage());
            }
        }
        return response;
    }
}
