package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.RegistrationConfiguration;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.RegistrationException;
import io.rocketbase.commons.exception.VerificationException;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.AppUserService;
import io.rocketbase.commons.service.email.EmailService;
import io.rocketbase.commons.service.VerificationLinkService;
import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import io.rocketbase.commons.service.VerificationLinkService.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@ConditionalOnProperty(value = "${auth.registration.enabled}", matchIfMissing = true)
public class RegistrationController implements BaseController{

    @Resource
    private RegistrationConfiguration registrationConfiguration;

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailService emailService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private VerificationLinkService verificationLinkService;

    @Resource
    private JwtTokenService jwtTokenService;

    @RequestMapping(method = RequestMethod.POST, path = "/auth/register", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<AppUserRead> register(HttpServletRequest request, @RequestBody @NotNull @Validated RegistrationRequest registration) {
        AppUser search = appUserService.getByUsername(registration.getUsername().toLowerCase());
        boolean emailUsed = appUserService.findByEmail(registration.getEmail().toLowerCase()).isPresent();
        if (search != null || emailUsed) {
            throw new RegistrationException(search != null, emailUsed);
        }
        AppUser entity = appUserService.registerUser(registration);

        if (registrationConfiguration.isEmailValidation()) {
            try {
                emailService.sentRegistrationEmail(entity, getBaseUrl(request));
            } catch (Exception e) {
                appUserService.delete(entity);
                throw e;
            }
        }

        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/verify", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JwtTokenBundle> verify(@RequestParam("verification") String verification) {
        VerificationToken token = verificationLinkService.parseKey(verification);
        if (!token.isValid(ActionType.VERIFICATION)) {
            throw new VerificationException();
        }

        AppUser entity = appUserService.registrationVerification(token.getUsername());

        return ResponseEntity.ok(jwtTokenService.generateTokenBundle(entity));
    }
}
