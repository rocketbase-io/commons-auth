package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailConfiguration;
import io.rocketbase.commons.model.AppUser;

public class SimpleMailContentConfig implements MailContentConfig {

    private EmailConfiguration emailConfiguration;

    public SimpleMailContentConfig(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public TemplateConfigBuilder register(AppUser user, String actionUrl) {
        return TemplateConfigBuilder.build()
                .title("Please Verify Your Account")
                .header("Verify Your Account")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("please verify your account by clicking the button")
                .action(actionUrl, "verify your account")
                .addGreeting(String.format("- %s", emailConfiguration.getServiceName()))
                .receiveNote(emailConfiguration.getServiceName(), emailConfiguration.getSupportEmail())
                .copyright(emailConfiguration.getCopyrightUrl(), emailConfiguration.getCopyrightName());
    }

    @Override
    public String registerSubject(AppUser user) {
        return String.format("%s Verify Your Account", emailConfiguration.getSubjectPrefix()).trim();
    }

    @Override
    public TemplateConfigBuilder forgotPassword(AppUser user, String actionUrl) {
        return TemplateConfigBuilder.build()
                .title("Reset Password")
                .headerWithStyling("You have submitted a password change request!", "fff", "E63946")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("if it was you, confirm the password change by clicking the button")
                .actionWithStyling(actionUrl, "confirm password change", "fff", "E63946")
                .addGreeting(String.format("- %s", emailConfiguration.getServiceName()))
                .receiveNote(emailConfiguration.getServiceName(), emailConfiguration.getSupportEmail())
                .copyright(emailConfiguration.getCopyrightUrl(), emailConfiguration.getCopyrightName());
    }

    @Override
    public String forgotPasswordSubject(AppUser user) {
        return String.format("%s Reset Password", emailConfiguration.getSubjectPrefix()).trim();
    }
}
