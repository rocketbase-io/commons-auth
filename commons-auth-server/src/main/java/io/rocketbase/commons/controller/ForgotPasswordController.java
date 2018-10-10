package io.rocketbase.commons.controller;


import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.service.AppUserForgotPasswordService;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class ForgotPasswordController implements BaseController {

    @Resource
    private AppUserForgotPasswordService appUserForgotPasswordService;

    @RequestMapping(value = "/auth/forgot-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> forgotPassword(HttpServletRequest request, @RequestBody @NotNull @Validated ForgotPasswordRequest forgotPassword) {
        if (forgotPassword.getEmail() == null || forgotPassword.getUsername() == null) {

        }
        appUserForgotPasswordService.requestPasswordReset(forgotPassword, getBaseUrl(request));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/auth/reset-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@RequestBody @NotNull @Validated PerformPasswordResetRequest performPasswordReset) {
        appUserForgotPasswordService.resetPassword(performPasswordReset);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
