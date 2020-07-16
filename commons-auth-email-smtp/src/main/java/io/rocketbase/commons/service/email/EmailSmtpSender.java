package io.rocketbase.commons.service.email;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class EmailSmtpSender implements EmailSender {

    private final JavaMailSender emailSender;

    @SneakyThrows
    @Override
    public void sentEmail(EmailAddress to, String subject, String html, String text, EmailAddress from) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(convert(to));
        helper.setSubject(subject);
        helper.setText(text, html);
        helper.setFrom(convert(from));
        emailSender.send(message);
    }

    @SneakyThrows
    protected InternetAddress convert(EmailAddress email) {
        return new InternetAddress(email.getEmail(), email.getName());
    }

}
