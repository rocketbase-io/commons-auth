package io.rocketbase.commons.config;

import io.rocketbase.commons.service.email.EmailSender;
import io.rocketbase.commons.service.email.EmailSmtpSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
public class AuthEmailSmtpAutoConfiguration {

    @Bean
    public EmailSender emailSender(@Autowired JavaMailSender javaMailSender) {
        return new EmailSmtpSender(javaMailSender);
    }
}
