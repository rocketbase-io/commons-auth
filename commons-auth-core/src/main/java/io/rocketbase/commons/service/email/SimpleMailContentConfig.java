package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimpleMailContentConfig implements MailContentConfig {

    final EmailProperties emailProperties;

    @Override
    public TemplateConfigBuilder register(AppUser user, String actionUrl) {
        return TemplateConfigBuilder.build()
                .title("Please Verify Your Account")
                .header("Verify Your Account")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("please verify your account by clicking the button")
                .action(actionUrl, "verify your account")
                .addGreeting(String.format("- %s", emailProperties.getServiceName()))
                .receiveNote(emailProperties.getServiceName(), emailProperties.getSupportEmail())
                .copyright(emailProperties.getCopyrightUrl(), emailProperties.getCopyrightName());
    }

    @Override
    public String registerSubject(AppUser user) {
        return String.format("%s Verify Your Account", emailProperties.getSubjectPrefix()).trim();
    }

    @Override
    public TemplateConfigBuilder forgotPassword(AppUser user, String actionUrl) {
        return TemplateConfigBuilder.build()
                .title("Reset Password")
                .headerWithStyling("You have submitted a password change request!", "fff", "E63946")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("if it was you, confirm the password change by clicking the button")
                .actionWithStyling(actionUrl, "confirm password change", "fff", "E63946")
                .addGreeting(String.format("- %s", emailProperties.getServiceName()))
                .receiveNote(emailProperties.getServiceName(), emailProperties.getSupportEmail())
                .copyright(emailProperties.getCopyrightUrl(), emailProperties.getCopyrightName());
    }

    @Override
    public String forgotPasswordSubject(AppUser user) {
        return String.format("%s Reset Password", emailProperties.getSubjectPrefix()).trim();
    }
}
