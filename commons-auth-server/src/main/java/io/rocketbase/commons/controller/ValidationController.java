package io.rocketbase.commons.controller;


import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.validation.*;
import io.rocketbase.commons.service.SimpleTokenService;
import io.rocketbase.commons.service.validation.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
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

    @RequestMapping(value = "/auth/validate/token", method = RequestMethod.POST)
    public ResponseEntity<ValidationResponse<TokenErrorCodes>> validateToken(@RequestBody String token) {
        boolean valid = SimpleTokenService.parseToken(token).isValid();
        return ResponseEntity.ok(new ValidationResponse<>(valid, valid ? Collections.emptySet() : Sets.newHashSet(TokenErrorCodes.EXPIRED)));
    }
}
