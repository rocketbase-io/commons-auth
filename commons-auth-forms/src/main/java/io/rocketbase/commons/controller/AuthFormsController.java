package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.ForgotPasswordResource;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@Slf4j
@Controller
public class AuthFormsController extends AbstractFormsController {

    private final AuthProperties authProperties;
    private final ForgotPasswordResource forgotPasswordResource;

    public AuthFormsController(String apiBaseUrl, FormsProperties formsProperties, RegistrationProperties registrationProperties, AuthProperties authProperties) {
        super(apiBaseUrl, formsProperties, registrationProperties);
        this.authProperties = authProperties;
        forgotPasswordResource = new ForgotPasswordResource(apiBaseUrl);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/logout")
    public String logoutForm(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/forgot")
    public String forgotForm() {
        return "forgot";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@ModelAttribute("forgotForm") @Validated ForgotPasswordRequest forgot,
                               BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (StringUtils.isEmpty(forgot.getEmail()) && StringUtils.isEmpty(forgot.getUsername())) {
                model.addAttribute("usernameOrEmailRequired", true);
            } else {
                try {
                    forgotPasswordResource.forgotPassword(forgot);
                    model.addAttribute("expiresAfter", authProperties.getPasswordResetExpiration());
                    return "forgot-submitted";
                } catch (BadRequestException badRequest) {
                    model.addAttribute("serviceException", badRequest.getErrorResponse().getMessage());
                } catch (Exception e) {
                    log.error("forgot password request - unexpected service exception: {}", e.getMessage());
                    model.addAttribute("serviceException", "unexpected service exception");
                }
            }
        }
        return "forgot";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(value = "verification", required = false) String verification, Model model) {
        model.addAttribute("resetPasswordForm", ResetPasswordForm.builder().verification(verification).build());
        try {
            getValidationResource().validateToken(verification);
            model.addAttribute("verificationValid", true);
        } catch (Exception e) {
            model.addAttribute("verificationValid", false);
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@ModelAttribute("resetPasswordForm") @Validated ResetPasswordForm resetPassword,
                                      BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (!resetPassword.getPassword().equals(resetPassword.getPasswordRepeat())) {
                model.addAttribute("passwordErrors", "password not the same!");
            } else {
                try {
                    forgotPasswordResource.resetPassword(resetPassword.toRequest());
                    return "reset-password-success";
                } catch (BadRequestException badRequest) {
                    Map<String, String> fields = badRequest.getErrorResponse().getFields();
                    if (fields.containsKey("password")) {
                        model.addAttribute("passwordErrors", fields.get("password"));
                    }
                } catch (Exception e) {
                    log.error("problem with the password-reset flow. {}", e.getMessage());
                }
            }
        }
        return "reset-password";
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = {"password", "passwordRepeat"})
    public static class ResetPasswordForm implements Serializable {

        private String verification;

        @NotEmpty
        private String password;

        @NotEmpty
        private String passwordRepeat;


        public PerformPasswordResetRequest toRequest() {
            return PerformPasswordResetRequest.builder()
                    .verification(verification)
                    .password(password)
                    .build();
        }

    }


}
