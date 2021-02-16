package io.rocketbase.commons.config;

import io.rocketbase.commons.service.email.EmailPostmarkSender;
import io.rocketbase.commons.service.email.EmailSender;
import io.rocketbase.mail.PostmarkClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@AutoConfigureBefore(name = "io.rocketbase.commons.config.AuthServiceAutoConfiguration")
public class AuthEmailPostmarkAutoConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = {"postmark.enabled"},
            matchIfMissing = true
    )
    public EmailSender emailSender(@Autowired PostmarkClient postmarkClient) {
        return new EmailPostmarkSender(postmarkClient);
    }
}
