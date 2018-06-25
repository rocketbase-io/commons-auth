package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.ColorStyle;
import io.rocketbase.commons.model.AppUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;

    @Override
    public HtmlTextEmail register(AppUser user, String actionUrl) {
        return EmailTemplateBuilder.builder()
                .header("Verify Your Account")
                .addText(String.format("Hi %s,", user.getUsername()))
                .addText("please verify your account by clicking the button")
                .addButton("verify your account", actionUrl)
                .addText(String.format("- %s", emailProperties.getServiceName()))
                .addFooter(String.format("You’re receiving this email because you have an account in %s.<br>" +
                        "If you are not sure why you’re receiving this, please contact us %s", emailProperties.getServiceName(), emailProperties.getSupportEmail()), true)
                .copyright(emailProperties.getCopyrightUrl(), emailProperties.getCopyrightName())
                .build();
    }

    @Override
    public String registerSubject(AppUser user) {
        return String.format("%s Verify Your Account", emailProperties.getSubjectPrefix()).trim();
    }

    @Override
    public HtmlTextEmail forgotPassword(AppUser user, String actionUrl) {
        return EmailTemplateBuilder.builder()
                .header("You have submitted a password change request!", new ColorStyle("fff", "E63946"))
                .addText(String.format("Hi %s,", user.getUsername()))
                .addText("if it was you, confirm the password change by clicking the button")
                .addButton("confirm password change", actionUrl, new ColorStyle("fff", "E63946"))
                .addText(String.format("- %s", emailProperties.getServiceName()))
                .addFooter(String.format("You’re receiving this email because you have an account in %s.<br>" +
                        "If you are not sure why you’re receiving this, please contact us %s", emailProperties.getServiceName(), emailProperties.getSupportEmail()), true)
                .copyright(emailProperties.getCopyrightUrl(), emailProperties.getCopyrightName())
                .build();
    }

    @Override
    public String forgotPasswordSubject(AppUser user) {
        return String.format("%s Reset Password", emailProperties.getSubjectPrefix()).trim();
    }
}
