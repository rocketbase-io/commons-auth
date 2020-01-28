package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.FormsProperties;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.InviteResource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Slf4j
@Controller
public class InviteFormsController extends AbstractFormsController {

    private final InviteResource inviteResource;
    @Value("${auth.forms.prefix:}")
    private String formsPrefix;

    public InviteFormsController(AuthProperties authProperties, FormsProperties formsProperties, RegistrationProperties registrationProperties) {
        super(authProperties, formsProperties, registrationProperties);
        inviteResource = new InviteResource(authProperties.getBaseUrl());
    }

    @GetMapping("${auth.forms.prefix:}/invite")
    public String verify(@RequestParam(value = "inviteId", required = false) String inviteId, Model model) {
        try {
            AppInviteRead info = inviteResource.verify(inviteId);
            model.addAttribute("validInvite", true);
            model.addAttribute("invitor", info.getInvitor());
            model.addAttribute("message", StringUtils.isEmpty(info.getMessage()) ? null : info.getMessage());
            model.addAttribute("inviteForm", new InviteForm(info));
        } catch (Exception e) {
            model.addAttribute("validInvite", false);
            model.addAttribute("inviteForm", ConfirmInviteRequest.builder().inviteId(inviteId).build());
        }
        return "invite";
    }

    @PostMapping("${auth.forms.prefix:}/invite")
    public String transformToUser(@ModelAttribute("inviteForm") @Validated InviteForm form,
                                  BindingResult bindingResult, Model model,
                                  HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            if (!form.getPassword().equals(form.getPasswordRepeat())) {
                model.addAttribute("passwordErrors", "password not the same!");
            } else {
                model.addAttribute("validInvite", true);
                try {
                    ConfirmInviteRequest confirmInviteRequest = form.toRequest();

                    AppUserRead user = inviteResource.transformToUser(confirmInviteRequest);
                    model.addAttribute("username", user.getUsername());
                    return "invite-success";
                } catch (BadRequestException badRequest) {
                    ErrorResponse errorResponse = badRequest.getErrorResponse();
                    if (errorResponse.hasField("username")) {
                        model.addAttribute("usernameErrors", errorResponse.getFields().get("username"));
                    }
                    if (errorResponse.hasField("email")) {
                        model.addAttribute("emailErrors", errorResponse.getFields().get("email"));
                    }
                    if (errorResponse.hasField("password")) {
                        model.addAttribute("passwordErrors", errorResponse.getFields().get("password"));
                    }
                } catch (Exception e) {
                    model.addAttribute("validInvite", false);
                    log.error("problem with the invite flow. {}", e.getMessage());
                }
            }
        } else {
            // check inviteId again
            try {
                inviteResource.verify(form.getInviteId());
                model.addAttribute("validInvite", true);
            } catch (Exception e) {
                model.addAttribute("validInvite", false);
            }
        }
        form.setPassword("");
        form.setPasswordRepeat("");
        return "invite";
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = {"password", "passwordRepeat"})
    public static class InviteForm implements Serializable {

        @NotNull
        private String inviteId;

        private String invitor;

        private String message;

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

        public InviteForm(AppInviteRead read) {
            setInviteId(read.getId());
            setInvitor(read.getInvitor());
            setMessage(read.getMessage());
            setFirstName(read.getFirstName());
            setLastName(read.getLastName());
            setEmail(read.getEmail());
        }

        public ConfirmInviteRequest toRequest() {
            return ConfirmInviteRequest.builder()
                    .inviteId(inviteId)
                    .username(username)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .build();
        }
    }


}
