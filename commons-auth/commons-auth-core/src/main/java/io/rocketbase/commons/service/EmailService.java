package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AuthConfiguration;
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
    public void sendRegistrationEmail(AppUser user, String applicationBaseUrl) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        TemplateConfigBuilder templateConfigBuilder = TemplateConfigBuilder.initDefault()
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
        helper.setText(htmlTextEmail.getHtml(), htmlTextEmail.getText());
        helper.setFrom(emailConfiguration.getFromEmail());

        emailSender.send(message);
    }

    private String buildActionUrl(String username, String applicationBaseUrl, ActionType actionType) {
        StringBuffer uriBuilder = new StringBuffer();
        if (emailConfiguration.getApplicationBaseUrl() != null) {
            uriBuilder.append(emailConfiguration.getApplicationBaseUrl());
        } else {
            uriBuilder.append(applicationBaseUrl);
        }
        if (uriBuilder.toString().endsWith("/")) {
            uriBuilder.deleteCharAt(uriBuilder.toString().length() - 1);
        }
        uriBuilder.append(actionType.getApiPath());

        uriBuilder.append("?verification=");

        long expiresInMinutes = actionType.equals(ActionType.VERIFICATION) ?
                registrationConfiguration.getEmailValidationExpiration() :
                emailConfiguration.getPasswordResetExpiration();

        uriBuilder.append(verificationLinkService.generateKey(username, actionType, expiresInMinutes));

        return uriBuilder.toString();
    }
}
