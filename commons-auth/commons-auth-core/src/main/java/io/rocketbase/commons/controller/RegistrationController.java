package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.RegistrationConfiguration;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.AppUserRead;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.dto.RegistrationRequest;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.security.JwtTokenService;
import io.rocketbase.commons.service.AppUserService;
import io.rocketbase.commons.service.EmailService;
import io.rocketbase.commons.service.VerificationLinkService;
import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import io.rocketbase.commons.service.VerificationLinkService.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@ConditionalOnProperty("${auth.registration.enabled}")
public class RegistrationController {

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
        AppUser search = appUserService.getByUsername(registration.getUsername());
        if (search != null) {
            return ResponseEntity.badRequest()
                    .header("error", "Username is already in use")
                    .build();
        }
        if (appUserService.findByEmail(registration.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .header("error", "Email is already in use")
                    .build();
        }

        AppUser entity = appUserService.registerUser(registration);

        if (registrationConfiguration.isEmailValidation()) {
            try {
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                emailService.sendRegistrationEmail(entity, baseUrl);
            } catch (Exception e) {
                appUserService.delete(entity);

                log.error("couldn't sent mail", e);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("error", "Couldn't sent email")
                        .build();
            }
        }

        return ResponseEntity.ok(appUserConverter.fromEntity(entity));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/verify", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JwtTokenBundle> verify(@RequestParam("verification") String verification) {
        VerificationToken token = verificationLinkService.parseKey(verification);
        if (!token.isValid(ActionType.VERIFICATION)) {
            return ResponseEntity.badRequest()
                    .header("error", "Verification is invalid")
                    .build();
        }

        AppUser entity = appUserService.registrationVerification(token.getUsername());

        return ResponseEntity.ok(jwtTokenService.generateTokenBundle(entity));
    }
}
