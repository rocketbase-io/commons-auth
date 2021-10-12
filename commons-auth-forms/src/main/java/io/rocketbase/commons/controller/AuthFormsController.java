package io.rocketbase.commons.controller;

import io.rocketbase.commons.api.ForgotPasswordApi;
import io.rocketbase.commons.api.ValidationApi;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.dto.forgot.PerformPasswordResetRequest;
import io.rocketbase.commons.dto.validation.TokenErrorCodes;
import io.rocketbase.commons.dto.validation.ValidationResponse;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.util.UrlParts;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

import static io.rocketbase.commons.handler.LoginSuccessCookieHandler.AUTH_REMEMBER;

@Slf4j
@Controller
public class AuthFormsController extends AbstractFormsController {

    private final AuthProperties authProperties;
    private final ForgotPasswordApi forgotPasswordApi;
    private final ValidationApi validationApi;

    @Value("${auth.forms.prefix:}")
    private String formsPrefix;

    public AuthFormsController(FormsProperties formsProperties, RegistrationProperties registrationProperties, AuthProperties authProperties, ForgotPasswordApi forgotPasswordApi, ValidationApi validationApi) {
        super(formsProperties, registrationProperties);
        this.authProperties = authProperties;
        this.forgotPasswordApi = forgotPasswordApi;
        this.validationApi = validationApi;
    }

    @GetMapping("${auth.forms.prefix:}/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("${auth.forms.prefix:}/logout")
    public String logoutForm(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            // remove refresh-token cookue
            Cookie authRemember = new Cookie(AUTH_REMEMBER, null);
            authRemember.setMaxAge(0);
            authRemember.setHttpOnly(false);
            authRemember.setPath("/");
            response.addCookie(authRemember);

            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("${auth.forms.prefix:}/forgot")
    public String forgotForm(Model model) {
        model.addAttribute("forgotForm", new ForgotPasswordRequest());
        return "forgot";
    }

    @PostMapping("${auth.forms.prefix:}/forgot")
    public String forgotSubmit(@ModelAttribute("forgotForm") @Validated ForgotPasswordRequest forgot,
                               BindingResult bindingResult, Model model,
                               HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (!StringUtils.hasText(forgot.getEmail()) && !StringUtils.hasText(forgot.getUsername())) {
                model.addAttribute("usernameOrEmailRequired", true);
            } else {
                try {
                    String resetPasswordUrl = UrlParts.getBaseUrl(request) + UrlParts.ensureStartsAndEndsWithSlash(formsPrefix) + "reset-password";
                    forgot.setResetPasswordUrl(resetPasswordUrl);
                    forgotPasswordApi.forgotPassword(forgot);
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

    @GetMapping("${auth.forms.prefix:}/reset-password")
    public String resetPasswordForm(@RequestParam(value = "verification", required = false) String verification, Model model) {
        String v = model.containsAttribute("verification") ? String.valueOf(model.getAttribute("verification")) : verification;
        prepareResetPasswordForm(model, v);
        return "reset-password";
    }

    public void prepareResetPasswordForm(Model model, String verification) {
        model.addAttribute("resetPasswordForm", ResetPasswordForm.builder().verification(verification).build());
        try {
            ValidationResponse<TokenErrorCodes> response = validationApi.validateToken(verification);
            model.addAttribute("verificationValid", response.isValid());
        } catch (Exception e) {
            model.addAttribute("verificationValid", false);
        }
    }

    @PostMapping("${auth.forms.prefix:}/reset-password")
    public String resetPasswordSubmit(@ModelAttribute("resetPasswordForm") @Validated ResetPasswordForm resetPassword,
                                      BindingResult bindingResult, Model model) {
        if (!bindingResult.hasErrors()) {
            if (!resetPassword.getPassword().equals(resetPassword.getPasswordRepeat())) {
                model.addAttribute("passwordErrors", "password not the same!");
            } else {
                try {
                    forgotPasswordApi.resetPassword(resetPassword.toRequest());
                    return "reset-password-success";
                } catch (BadRequestException badRequest) {
                    model.addAttribute("verification", resetPassword.getVerification());
                    prepareResetPasswordForm(model, resetPassword.getVerification());

                    if (badRequest.getErrorResponse().hasField("password")) {
                        model.addAttribute("passwordErrors", badRequest.getErrorResponse().getFields().get("password"));
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
