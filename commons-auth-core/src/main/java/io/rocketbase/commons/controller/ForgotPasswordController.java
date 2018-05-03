package io.rocketbase.commons.controller;


import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.exception.UnknownUserException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.AppUserService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.VerificationLinkService;
import io.rocketbase.commons.service.VerificationLinkService.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class ForgotPasswordController {

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private VerificationLinkService verificationLinkService;


    @RequestMapping(value = "/auth/forgot-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> forgotPassword(HttpServletRequest request, @RequestBody @NotNull @Validated ForgotPasswordRequest forgotPassword) {
        Optional<AppUser> optional = appUserService.findByEmail(forgotPassword.getEmail().toLowerCase());
        if (!optional.isPresent() || !optional.get().isEnabled()) {
            throw new UnknownUserException();
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        emailService.sentForgotPasswordEmail(optional.get(), baseUrl);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/reset-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @NotNull @Validated PerformPasswordResetRequest performPasswordReset) {
        VerificationToken token = verificationLinkService.parseKey(performPasswordReset.getVerification());
        if (!token.isValid(VerificationLinkService.ActionType.PASSWORD_RESET)) {
            throw new VerificationException();
        }
        AppUser user = appUserService.getByUsername(token.getUsername());
        if (user == null || !user.isEnabled()) {
            throw new UnknownUserException();
        }
        appUserService.updatePassword(user.getUsername(), performPasswordReset.getPassword());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
