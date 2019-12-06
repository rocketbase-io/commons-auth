package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.resource.ValidationResource;
import lombok.Getter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractFormsController {

    @Getter
    private final AuthProperties authProperties;
    @Getter
    private final FormsProperties formsProperties;
    @Getter
    private final RegistrationProperties registrationProperties;

    @Getter
    private ValidationResource validationResource;

    public AbstractFormsController(AuthProperties authProperties, FormsProperties formsProperties, RegistrationProperties registrationProperties) {
        this.authProperties = authProperties;
        this.formsProperties = formsProperties;
        this.registrationProperties = registrationProperties;

        validationResource = new ValidationResource(authProperties.getBaseUrl());
    }

    @ModelAttribute
    public void populateDefaults(Model model) {
        model.addAttribute("title", formsProperties.getTitle());
        model.addAttribute("logoSrc", formsProperties.getLogoSrc());
        model.addAttribute("registrationEnabled", registrationProperties.isEnabled());
    }


    /**
     * get baseUrl without / at the end
     *
     * @param request current HttpServletRequest
     * @return baseUrl without /
     */
    public String getBaseUrl(HttpServletRequest request) {
        String result = request.getScheme() + "://" + request.getServerName();
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443) {
            result += ":" + serverPort;
        }
        result += request.getContextPath();
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

}
