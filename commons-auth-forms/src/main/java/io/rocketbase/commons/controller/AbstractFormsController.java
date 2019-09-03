package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.resource.ValidationResource;
import lombok.Getter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

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

}
