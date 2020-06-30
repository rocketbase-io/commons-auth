package io.rocketbase.commons.controller;


import io.rocketbase.commons.converter.ValidationConverter;
import io.rocketbase.commons.dto.validation.*;
import io.rocketbase.commons.exception.ValidationErrorCode;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("${auth.prefix:}")
@RequiredArgsConstructor
public class ValidationController implements BaseController {

    @Resource
    private ValidationService validationService;


    @RequestMapping(value = "/auth/validate/password", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<PasswordErrorCodes>> validatePassword(@RequestBody String password) {
        Set<ValidationErrorCode<PasswordErrorCodes>> passwordErrorCodes = validationService.getPasswordValidationDetails(password);
        return ResponseEntity.ok(ValidationConverter.convert(passwordErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/username", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<UsernameErrorCodes>> validateUsername(@RequestBody String username) {
        Set<ValidationErrorCode<UsernameErrorCodes>> usernameErrorCodes = validationService.getUsernameValidationDetails(username);
        return ResponseEntity.ok(ValidationConverter.convert(usernameErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/email", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<EmailErrorCodes>> validateEmail(@RequestBody String email) {
        Set<ValidationErrorCode<EmailErrorCodes>> emailErrorCodes = validationService.getEmailValidationDetails(email);
        return ResponseEntity.ok(ValidationConverter.convert(emailErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/token", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<TokenErrorCodes>> validateToken(@RequestBody String token) {
        Set<ValidationErrorCode<TokenErrorCodes>> tokenErrorCodes = validationService.getTokenValidationDetails(token);
        return ResponseEntity.ok(ValidationConverter.convert(tokenErrorCodes));
    }
}
