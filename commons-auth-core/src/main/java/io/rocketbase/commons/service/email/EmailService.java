package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.email.EmailTemplateService.HtmlTextEmail;
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


    final InternetAddress from;

    @Resource
    private JavaMailSender emailSender;

    @Resource
    private MailContentConfig mailContentConfig;

    @Resource
    private EmailTemplateService emailTemplateService;

    @SneakyThrows
    public void sentRegistrationEmail(AppUser user, String verificationUrl) {
        TemplateConfigBuilder register = mailContentConfig.register(user, verificationUrl);
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(register);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.registerSubject(user), htmlTextEmail, from);
    }

    @SneakyThrows
    public void sentForgotPasswordEmail(AppUser user, String verificationUrl) {
        TemplateConfigBuilder forgotPassword = mailContentConfig.forgotPassword(user, verificationUrl);
        HtmlTextEmail htmlTextEmail = emailTemplateService.buildHtmlTextTemplate(forgotPassword);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), htmlTextEmail, from);
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

}
