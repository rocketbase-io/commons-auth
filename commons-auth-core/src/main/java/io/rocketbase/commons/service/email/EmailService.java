package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailConfiguration;
import io.rocketbase.commons.config.RegistrationConfiguration;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.VerificationLinkService;
import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import io.rocketbase.commons.service.email.EmailTemplateService.HtmlTextEmail;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Resource
    private JavaMailSender emailSender;

    @Resource
    private MailContentConfig mailContentConfig;

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
        TemplateConfigBuilder register = mailContentConfig.register(user, buildActionUrl(user.getUsername(), applicationBaseUrl, ActionType.VERIFICATION));
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(register);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.registerSubject(user), htmlTextEmail, new InternetAddress(emailConfiguration.getFromEmail()));
    }

    @SneakyThrows
    public void sentForgotPasswordEmail(AppUser user, String applicationBaseUrl) {
        TemplateConfigBuilder forgotPassword = mailContentConfig.forgotPassword(user, buildActionUrl(user.getUsername(), applicationBaseUrl, ActionType.PASSWORD_RESET));
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(forgotPassword);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), htmlTextEmail, new InternetAddress(emailConfiguration.getFromEmail()));
    }

    @SneakyThrows
    public void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail, InternetAddress from) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
        helper.setFrom(from);
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
