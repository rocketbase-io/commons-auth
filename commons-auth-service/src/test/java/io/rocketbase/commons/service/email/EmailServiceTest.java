package io.rocketbase.commons.service.email;

import com.icegreen.greenmail.util.GreenMailUtil;
import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.test.BaseUserIntegrationTest;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EmailServiceTest extends BaseUserIntegrationTest {

    @Resource
    private EmailService emailService;

    @Test
    public void simpleSendRegistrationEmail() throws Exception {
        // given
        AppUserEntity user = getAppUser();

        EmailProperties emailProperties = new EmailProperties();

        // when
        emailService.sentRegistrationEmail(user, "http://localhost:8080/?verification=token");

        // then
        MimeMessage[] receivedMessages = getSmtpServerRule().getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];

        assertThat(current.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(current.getFrom()[0].toString(), containsString(emailProperties.getFromEmail()));
        assertThat(current.getRecipients(Message.RecipientType.TO)[0].toString(), containsString(user.getEmail()));
        assertThat(current.getContent(), instanceOf(MimeMultipart.class));
        assertThat(((MimeMultipart) current.getContent()).getBodyPart(0).getSize(), greaterThan(0));
    }

    @Test
    public void germanRegistrationEmail() throws Exception {
        // given
        AppUserEntity user = getAppUser();

        EmailProperties emailProperties = new EmailProperties();
        LocaleContextHolder.setLocale(Locale.GERMAN);

        // when
        emailService.sentRegistrationEmail(user, "http://localhost:8080/?verification=token");

        // then
        MimeMessage[] receivedMessages = getSmtpServerRule().getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];

        assertThat(current.getSubject(), is(String.format("%s Registierung bestätigen", emailProperties.getSubjectPrefix())));
        assertThat(GreenMailUtil.getBody(current), containsString("Sie Ihre Registrierung durch einen Klick auf den"));
    }
}