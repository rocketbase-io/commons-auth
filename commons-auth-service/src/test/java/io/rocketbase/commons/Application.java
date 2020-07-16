package io.rocketbase.commons;

import io.rocketbase.commons.service.email.EmailSender;
import io.rocketbase.commons.test.EmailSenderTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    public EmailSender emailSender() {
        return new EmailSenderTest();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}