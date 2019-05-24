package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.ForgotPasswordResource;
import io.rocketbase.commons.resource.RegistrationResource;
import io.rocketbase.commons.resource.ValidationResource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthFormsController {

    private final String apiBaseUrl;
    private final FormsProperties formsProperties;
    private final RegistrationProperties registrationProperties;

    private ValidationResource validationResource;
    private RegistrationResource registrationResource;
    private ForgotPasswordResource forgotPasswordResource;

    @PostConstruct
    public void postConstruct() {
        validationResource = new ValidationResource(apiBaseUrl);
        registrationResource = new RegistrationResource(apiBaseUrl);
        forgotPasswordResource = new ForgotPasswordResource(apiBaseUrl);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutForm(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationSubmit(@ModelAttribute("registrationForm") @Validated RegistrationForm registration,
                                     BindingResult bindingResult, Model model) {

        if (!bindingResult.hasErrors()) {
            if (!registration.getPassword().equals(registration.getPasswordRepeat())) {
                model.addAttribute("passwordErrors", "password not the same!");
            } else {
                try {
                    AppUserRead user = registrationResource.register(registration.toRequest());
                    model.addAttribute("needsVerification", !user.isEnabled());
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

    @GetMapping("/forgot")
    public String forgotForm() {
        return "forgot";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(value="token",required=false) String token) {
        return "forgot";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@ModelAttribute("forgotForm") @Validated ForgotPasswordRequest forgot,
                               BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (StringUtils.isEmpty(forgot.getEmail()) && StringUtils.isEmpty(forgot.getUsername())) {
                model.addAttribute("usernameOrEmailRequired", true);
            }

        }
        return "forgot";
    }

    @ModelAttribute
    public void populateDefaults(Model model) {
        model.addAttribute("title", formsProperties.getTitle());
        model.addAttribute("logoSrc", formsProperties.getLogoSrc());
        model.addAttribute("registrationEnabled", registrationProperties.isEnabled());
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
