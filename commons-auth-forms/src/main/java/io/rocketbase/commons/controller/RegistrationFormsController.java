package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.RegistrationResource;
import io.rocketbase.commons.util.UrlParts;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@Slf4j
@Controller
public class RegistrationFormsController extends AbstractFormsController {

    @Value("${auth.forms.prefix:}")
    private String formsPrefix;

    private final RegistrationResource registrationResource;

    public RegistrationFormsController(AuthProperties authProperties, FormsProperties formsProperties, RegistrationProperties registrationProperties) {
        super(authProperties, formsProperties, registrationProperties);
        registrationResource = new RegistrationResource(authProperties.getBaseUrl());
    }

    @GetMapping("${auth.forms.prefix:}/registration")
    public String registration(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("${auth.forms.prefix:}/registration")
    public String registrationSubmit(@ModelAttribute("registrationForm") @Validated RegistrationForm registration,
                                     BindingResult bindingResult, Model model,
                                     HttpServletRequest request) {

        if (!bindingResult.hasErrors()) {
            if (!registration.getPassword().equals(registration.getPasswordRepeat())) {
                model.addAttribute("passwordErrors", "password not the same!");
            } else {
                try {
                    RegistrationRequest registrationRequest = registration.toRequest();
                    String verificationUrl = getBaseUrl(request) + UrlParts.ensureStartsAndEndsWithSlash(formsPrefix) + "verification";
                    registrationRequest.setVerificationUrl(verificationUrl);

                    AppUserRead user = registrationResource.register(registrationRequest);
                    model.addAttribute("needsVerification", !user.isEnabled());
                    model.addAttribute("expiresAfter", getRegistrationProperties().getVerificationExpiration());
                    return "registration-success";
                } catch (BadRequestException badRequest) {
                    Map<String, String> fields = badRequest.getErrorResponse().getFields();
                    if (fields.containsKey("username")) {
                        model.addAttribute("usernameErrors", fields.get("username"));
                    }
                    if (fields.containsKey("email")) {
                        model.addAttribute("emailErrors", fields.get("email"));
                    }
                    if (fields.containsKey("password")) {
                        model.addAttribute("passwordErrors", fields.get("password"));
                    }
                } catch (Exception e) {
                    log.error("problem with the registration flow. {}", e.getMessage());
                }
            }
        }
        registration.setPassword("");
        registration.setPasswordRepeat("");
        return "registration";
    }

    @GetMapping("${auth.forms.prefix:}/verification")
    public String verification(@RequestParam(value = "verification", required = false) String verification, Model model) {
        try {
            registrationResource.verify(verification);
            model.addAttribute("successfull", true);
        } catch (Exception e) {
            model.addAttribute("successfull", false);
        }
        return "registration-verification";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = {"password", "passwordRepeat"})
    public static class RegistrationForm implements Serializable {

        @NotEmpty
        private String username;

        private String firstName;

        private String lastName;

        @NotEmpty
        @Email
        private String email;

        @NotEmpty
        private String password;

        private String passwordRepeat;

        public RegistrationRequest toRequest() {
            return RegistrationRequest.builder()
                    .username(username)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password)
                    .build();
        }
    }


}
