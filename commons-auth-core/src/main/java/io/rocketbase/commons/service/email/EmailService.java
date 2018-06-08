package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.email.EmailTemplateService.HtmlTextEmail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class EmailService {

    final AuthProperties authProperties;
    final EmailProperties emailProperties;

    @Resource
    private JavaMailSender emailSender;

    @Resource
    private MailContentConfig mailContentConfig;

    @Resource
    private EmailTemplateService emailTemplateService;

    @SneakyThrows
    public void sentRegistrationEmail(AppUser user, String applicationBaseUrl, String token) {

        TemplateConfigBuilder register = mailContentConfig.register(user, buildActionUrl(applicationBaseUrl, ActionType.VERIFICATION, token));
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(register);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.registerSubject(user), htmlTextEmail, new InternetAddress(emailProperties.getFromEmail()));
    }

    @SneakyThrows
    public void sentForgotPasswordEmail(AppUser user, String applicationBaseUrl, String token) {
        TemplateConfigBuilder forgotPassword = mailContentConfig.forgotPassword(user, buildActionUrl(applicationBaseUrl, ActionType.PASSWORD_RESET, token));
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(forgotPassword);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), htmlTextEmail, new InternetAddress(emailProperties.getFromEmail()));
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

    protected String buildActionUrl(String applicationBaseUrl, ActionType actionType, String token) {
        String uri = handleBaseUrl(applicationBaseUrl, actionType);
        uri += uri.contains("?") ? "&" : "?";
        uri += "verification=" + token;
        return uri;
    }

    private String handleBaseUrl(String applicationBaseUrl, ActionType actionType) {
        String configuredUrl = null;
        switch (actionType) {
            case VERIFICATION:
                configuredUrl = authProperties.getVerificationUrl();
                break;
            case PASSWORD_RESET:
                configuredUrl = authProperties.getPasswordResetUrl();
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

    enum ActionType {

        VERIFICATION("/auth/verify"), PASSWORD_RESET(("/auth/reset-password/index.html"));

        @Getter
        private String apiPath;

        ActionType(String apiPath) {
            this.apiPath = apiPath;
        }

    }
}
