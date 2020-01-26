package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.ColorStyle;
import io.rocketbase.commons.model.AppUserEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;

    @Override
    public HtmlTextEmail register(AppUserEntity user, String actionUrl) {
        return EmailTemplateBuilder.builder()
                .header("Verify Your Account").and()
                .addText(String.format("Hi %s,", user.getUsername())).and()
                .addText("please verify your account by clicking the button").and()
                .addButton("verify your account", actionUrl).center().and()
                .addText(String.format("- %s", emailProperties.getServiceName())).and()
                .addFooter(String.format("You’re receiving this email because you have an account in %s.<br>" +
                        "If you are not sure why you’re receiving this, please contact us %s", emailProperties.getServiceName(), emailProperties.getSupportEmail())).and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String registerSubject(AppUserEntity user) {
        return String.format("%s Verify Your Account", emailProperties.getSubjectPrefix()).trim();
    }

    @Override
    public HtmlTextEmail forgotPassword(AppUserEntity user, String actionUrl) {
        return EmailTemplateBuilder.builder()
                .header("You have submitted a password change request!").color(new ColorStyle("fff", "E63946")).and()
                .addText(String.format("Hi %s,", user.getUsername())).and()
                .addText("if it was you, confirm the password change by clicking the button").and()
                .addButton("confirm password change", actionUrl).color(new ColorStyle("fff", "E63946")).center().and()
                .addText(String.format("- %s", emailProperties.getServiceName())).and()
                .addFooter(String.format("You’re receiving this email because you have an account in %s.<br>" +
                        "If you are not sure why you’re receiving this, please contact us %s", emailProperties.getServiceName(), emailProperties.getSupportEmail())).and()
                .copyright(emailProperties.getCopyrightName()).url(emailProperties.getCopyrightUrl())
                .build();
    }

    @Override
    public String forgotPasswordSubject(AppUserEntity user) {
        return String.format("%s Reset Password", emailProperties.getSubjectPrefix()).trim();
    }
}
