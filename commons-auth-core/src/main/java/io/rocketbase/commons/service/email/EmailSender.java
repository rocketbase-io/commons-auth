package io.rocketbase.commons.service.email;

public interface EmailSender {

    void sentEmail(EmailAddress to, String subject, String html, String text, EmailAddress from);
}
