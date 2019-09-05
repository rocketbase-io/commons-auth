package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.registration.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@ConditionalOnExpression(value = "${auth.registration.enabled:true}")
@RequestMapping("${auth.prefix:}")
public class RegistrationController implements BaseController {

    @Resource
    private RegistrationService registrationService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private JwtTokenService jwtTokenService;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/register", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUserRead> register(HttpServletRequest request, @RequestBody @NotNull @Validated RegistrationRequest registration) {
        AppUserEntity entity = registrationService.register(registration, getBaseUrl(request));
        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/verify")
    @ResponseBody
    public ResponseEntity<JwtTokenBundle> verify(@RequestParam("verification") String verification) {
        AppUserEntity entity = registrationService.verifyRegistration(verification);

        return ResponseEntity.ok(jwtTokenService.generateTokenBundle(entity));
    }
}
