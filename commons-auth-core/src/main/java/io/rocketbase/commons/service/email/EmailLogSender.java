package io.rocketbase.commons.service.email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailLogSender implements EmailSender {

    @Override
    public void sentEmail(EmailAddress to, String subject, String html, String text, EmailAddress from) {
        log.warn("sentEmail to: {}, subject: {}", to.getEmail(), subject);
    }
}
