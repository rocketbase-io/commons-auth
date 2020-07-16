package io.rocketbase.commons.test;

import io.rocketbase.commons.service.email.EmailAddress;
import io.rocketbase.commons.service.email.EmailSender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class EmailSenderTest implements EmailSender {

    private EmailAddress to;
    private String subject;
    private String html;
    private String text;
    private EmailAddress from;


    @Override
    public void sentEmail(EmailAddress to, String subject, String html, String text, EmailAddress from) {
        this.to = to;
        this.subject = subject;
        this.html = html;
        this.text = text;
        this.from = from;
    }
}
