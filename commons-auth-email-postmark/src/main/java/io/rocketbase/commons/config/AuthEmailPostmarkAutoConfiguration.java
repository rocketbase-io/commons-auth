package io.rocketbase.commons.config;

import io.rocketbase.commons.service.email.EmailPostmarkSender;
import io.rocketbase.commons.service.email.EmailSender;
import io.rocketbase.mail.PostmarkClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthEmailPostmarkAutoConfiguration {

    @Bean
    public EmailSender emailSender(@Autowired PostmarkClient postmarkClient) {
        return new EmailPostmarkSender(postmarkClient);
    }
}
