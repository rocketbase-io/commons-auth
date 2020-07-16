package io.rocketbase.commons;

import io.rocketbase.commons.service.email.EmailSender;
import io.rocketbase.commons.test.EmailSenderTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public EmailSender emailSender() {
        return new EmailSenderTest();
    }
}
