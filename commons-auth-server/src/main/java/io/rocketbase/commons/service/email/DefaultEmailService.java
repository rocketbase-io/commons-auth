package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class DefaultEmailService implements EmailService {

    @Getter
    final InternetAddress from;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MailContentConfig mailContentConfig;

    private JavaMailSender emailSender;

    @PostConstruct
    public void init() {
        try {
            emailSender = applicationContext.getBean(JavaMailSender.class);
        } catch (BeansException e) {
            log.error("JavaMailSender is missing. Mails will only get logged. For production please configure spring-boot-starter-mail.");
        }
    }

    @Override
    @SneakyThrows
    public void sentRegistrationEmail(AppUser user, String verificationUrl) {
        HtmlTextEmail htmlTextEmail = mailContentConfig.register(user, verificationUrl);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.registerSubject(user), htmlTextEmail);
    }

    @Override
    @SneakyThrows
    public void sentForgotPasswordEmail(AppUser user, String verificationUrl) {
        HtmlTextEmail htmlTextEmail = mailContentConfig.forgotPassword(user, verificationUrl);

        sentEmail(new InternetAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), htmlTextEmail);
    }

    @Override
    public void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail) {
        sentEmail(to, subject, htmlTextEmail, getFrom());
    }

    @Override
    @SneakyThrows
    public void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail, InternetAddress from) {
        if (emailSender == null) {
            log.error("JavaMailSender is missing. SentEmail will only get logged.\nsentEmail: {} to: {} message:\n", subject, to.getAddress(), htmlTextEmail.getText());
        } else {
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

}
