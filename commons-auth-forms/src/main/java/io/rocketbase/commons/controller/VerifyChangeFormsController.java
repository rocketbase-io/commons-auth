package io.rocketbase.commons.controller;

import io.rocketbase.commons.api.AuthenticationApi;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class VerifyChangeFormsController extends AbstractFormsController {

    private final AuthenticationApi authenticationApi;

    public VerifyChangeFormsController(FormsProperties formsProperties, RegistrationProperties registrationProperties, AuthenticationApi authenticationApi) {
        super(formsProperties, registrationProperties);
        this.authenticationApi = authenticationApi;
    }

    @GetMapping("${auth.forms.prefix:}/verify-email")
    public String verify(@RequestParam(value = "verification", required = false) String verification, Model model) {
        try {
            authenticationApi.verifyEmail(verification);
            model.addAttribute("valid", true);
        } catch (Exception e) {
            model.addAttribute("valid", false);
        }
        return "email-change-success";
    }

}
