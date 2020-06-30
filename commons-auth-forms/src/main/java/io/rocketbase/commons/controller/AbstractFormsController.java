package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import lombok.Getter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractFormsController {

    @Getter
    private final FormsProperties formsProperties;
    @Getter
    private final RegistrationProperties registrationProperties;

    public AbstractFormsController(FormsProperties formsProperties, RegistrationProperties registrationProperties) {
        this.formsProperties = formsProperties;
        this.registrationProperties = registrationProperties;
    }

    @ModelAttribute
    public void populateDefaults(Model model) {
        model.addAttribute("title", formsProperties.getTitle());
        model.addAttribute("logoSrc", formsProperties.getLogoSrc());
        model.addAttribute("registrationEnabled", registrationProperties.isEnabled());
    }

}
