package io.rocketbase.commons.controller;


import io.rocketbase.commons.dto.validation.EmailErrorCodes;
import io.rocketbase.commons.dto.validation.PasswordErrorCodes;
import io.rocketbase.commons.dto.validation.UsernameErrorCodes;
import io.rocketbase.commons.dto.validation.ValidationResponse;
import io.rocketbase.commons.service.ValidationService;
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
public class ValidationController implements BaseController {

    @Resource
    private ValidationService validationService;

    @RequestMapping(value = "/auth/validate/password", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<PasswordErrorCodes>> validatePassword(@RequestBody String password) {
        Set<PasswordErrorCodes> passwordErrorCodes = validationService.getPasswordValidationDetails(password);
        return ResponseEntity.ok(new ValidationResponse<>(passwordErrorCodes.isEmpty(), passwordErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/username", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<UsernameErrorCodes>> validateUsername(@RequestBody String username) {
        Set<UsernameErrorCodes> usernameErrorCodes = validationService.getUsernameValidationDetails(username);
        return ResponseEntity.ok(new ValidationResponse<>(usernameErrorCodes.isEmpty(), usernameErrorCodes));
    }

    @RequestMapping(value = "/auth/validate/email", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<EmailErrorCodes>> validateEmail(@RequestBody String email) {
        Set<EmailErrorCodes> emailErrorCodes = validationService.getEmailValidationDetails(email);
        return ResponseEntity.ok(new ValidationResponse<>(emailErrorCodes.isEmpty(), emailErrorCodes));
    }
}
