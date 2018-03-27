package io.rocketbase.commons.service;

import io.rocketbase.commons.config.EmailConfiguration;
import io.rocketbase.commons.config.RegistrationConfiguration;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.EmailTemplateService.HtmlTextEmail;
import io.rocketbase.commons.service.EmailTemplateService.TemplateConfigBuilder;
import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Resource
    private JavaMailSender emailSender;

    @Resource
    private EmailConfiguration emailConfiguration;

    @Resource
    private RegistrationConfiguration registrationConfiguration;

    @Resource
    private EmailTemplateService emailTemplateService;

    @Resource
    private VerificationLinkService verificationLinkService;


    @SneakyThrows
    public void sentRegistrationEmail(AppUser user, String applicationBaseUrl) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        TemplateConfigBuilder templateConfigBuilder = TemplateConfigBuilder.build()
                .title("Please Verify Your Account")
                .header("Verify Your Account")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("please verify your account by clicking the button")
                .action(buildActionUrl(user.getUsername(), applicationBaseUrl, ActionType.VERIFICATION), "verify your account")
                .addGreeting(String.format("- %s", emailConfiguration.getServiceName()))
                .receiveNote(emailConfiguration.getServiceName(), emailConfiguration.getSupportEmail())
                .copyright(emailConfiguration.getCopyrightUrl(), emailConfiguration.getCopyrightName());

        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(templateConfigBuilder);

        helper.setTo(user.getEmail());
        helper.setSubject(String.format("%s Verify Your Account", emailConfiguration.getSubjectPrefix()).trim());
        helper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
        helper.setFrom(emailConfiguration.getFromEmail());

        emailSender.send(message);
    }

    @SneakyThrows
    public void sentForgotPasswordEmail(AppUser user, String applicationBaseUrl) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());


        String url = buildActionUrl(user.getUsername(), applicationBaseUrl, ActionType.PASSWORD_RESET);

        TemplateConfigBuilder templateConfigBuilder = TemplateConfigBuilder.build()
                .title("Reset Password")
                .headerWithStyling("You have submitted a password change request!", "fff", "E63946")
                .addLine(String.format("Hi %s,", user.getUsername()))
                .addLine("if it was you, confirm the password change by clicking the button")
                .actionWithStyling(url, "confirm password change", "fff", "E63946")
                .addGreeting(String.format("- %s", emailConfiguration.getServiceName()))
                .receiveNote(emailConfiguration.getServiceName(), emailConfiguration.getSupportEmail())
                .copyright(emailConfiguration.getCopyrightUrl(), emailConfiguration.getCopyrightName());

        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(templateConfigBuilder);

        helper.setTo(user.getEmail());
        helper.setSubject(String.format("%s Reset Password", emailConfiguration.getSubjectPrefix()).trim());
        helper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
        helper.setFrom(emailConfiguration.getFromEmail());

        emailSender.send(message);
    }

    protected String buildActionUrl(String username, String applicationBaseUrl, ActionType actionType) {
        String uri = handleBaseUrl(applicationBaseUrl, actionType);
        uri += uri.contains("?") ? "&" : "?";
        uri += "verification=";
        uri += verificationLinkService.generateKey(username, actionType, getExpiresInMinutes(actionType));
        return uri;
    }

    private long getExpiresInMinutes(ActionType actionType) {
        Long expiresInMinutes = null;
        switch (actionType) {
            case VERIFICATION:
                expiresInMinutes = registrationConfiguration.getEmailValidationExpiration();
                break;
            case PASSWORD_RESET:
                expiresInMinutes = emailConfiguration.getPasswordResetExpiration();
                break;
        }
        return expiresInMinutes;
    }

    private String handleBaseUrl(String applicationBaseUrl, ActionType actionType) {
        String configuredUrl = null;
        switch (actionType) {
            case VERIFICATION:
                configuredUrl = emailConfiguration.getVerificationUrl();
                break;
            case PASSWORD_RESET:
                configuredUrl = emailConfiguration.getPasswordResetUrl();
                break;
        }

        String result;
        if (configuredUrl != null) {
            // in case of configured url this will have a full qualified url to a custom UI
            result = configuredUrl;
        } else {
            result = applicationBaseUrl;
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            result += actionType.getApiPath();
        }
        return result;
    }
}
