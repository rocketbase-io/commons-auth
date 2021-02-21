package io.rocketbase.commons.controller;


import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.service.forgot.AppUserForgotPasswordService;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("${auth.prefix:}")
public class ForgotPasswordController implements BaseController {

    @Resource
    private AppUserForgotPasswordService appUserForgotPasswordService;

    @Resource
    private AppUserConverter appUserConverter;

    @RequestMapping(value = "/auth/forgot-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpirationInfo<Void>> forgotPassword(HttpServletRequest request, @RequestBody @NotNull @Validated ForgotPasswordRequest forgotPassword) {
        return ResponseEntity.ok(appUserForgotPasswordService.requestPasswordReset(forgotPassword, getBaseUrl(request)));
    }

    @RequestMapping(value = "/auth/reset-password", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserRead> resetPassword(@RequestBody @NotNull @Validated PerformPasswordResetRequest performPasswordReset) {
        AppUserEntity entity = appUserForgotPasswordService.resetPassword(performPasswordReset);
        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }
}
