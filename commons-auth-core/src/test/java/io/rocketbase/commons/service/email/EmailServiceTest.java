package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class EmailServiceTest extends BaseIntegrationTest {

    @Resource
    private EmailService emailService;

    @Test
    public void simpleSendRegistrationEmail() throws Exception {
        // given
        AppUser user = getAppUser();

        EmailProperties emailProperties = new EmailProperties();

        // when
        emailService.sentRegistrationEmail(user, "http://localhost:8080/", "token");

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
    public void buildActionUrlWithNullConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(null);

        // when
        String result = new EmailService(authProperties, new EmailProperties())
                .buildActionUrl(applicationBaseUrl, EmailService.ActionType.VERIFICATION, "token");

        // then
        assertThat(result, startsWith(applicationBaseUrl + EmailService.ActionType.VERIFICATION.getApiPath()));
    }

    @Test
    public void buildActionUrlWithFilledConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/";
        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(configBaseUrl);

        // when
        String result = new EmailService(authProperties, new EmailProperties())
                .buildActionUrl(applicationBaseUrl, EmailService.ActionType.VERIFICATION, "token");

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?verification="));
    }

    @Test
    public void buildActionUrlWithFilledConfigQueryParam() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/?action=submit";
        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(configBaseUrl);

        // when
        String result = new EmailService(authProperties, new EmailProperties())
                .buildActionUrl(applicationBaseUrl, EmailService.ActionType.VERIFICATION, "token");

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?action=submit&verification="));
    }
}