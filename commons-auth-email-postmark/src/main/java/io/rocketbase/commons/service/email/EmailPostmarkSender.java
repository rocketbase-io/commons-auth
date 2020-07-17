package io.rocketbase.commons.service.email;

import io.rocketbase.mail.PostmarkClient;
import io.rocketbase.mail.dto.Message;
import io.rocketbase.mail.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class EmailPostmarkSender implements EmailSender {

    private final PostmarkClient postmarkClient;

    @SneakyThrows
    @Override
    public void sentEmail(EmailAddress to, String subject, String html, String text, EmailAddress from) {
        Message message = new Message();
        message.setFrom(convert(from));
        message.setTo(convert(to));
        message.setSubject(subject);
        message.setHtmlBody(html);
        message.setTextBody(text);

        MessageResponse response = postmarkClient.deliverMessage(message);
    }

    protected io.rocketbase.mail.dto.EmailAddress convert(EmailAddress mail) {
        return new io.rocketbase.mail.dto.EmailAddress(mail.getEmail(), mail.getName());
    }

}
