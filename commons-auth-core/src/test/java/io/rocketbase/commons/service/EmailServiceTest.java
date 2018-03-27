package io.rocketbase.commons.service;

import io.rocketbase.commons.config.EmailConfiguration;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.service.VerificationLinkService.ActionType;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

public class EmailServiceTest extends BaseIntegrationTest {

    @MockBean
    private EmailConfiguration emailConfiguration;

    @Resource
    private EmailService emailService;

    @Test
    public void simpleSendRegistrationEmail() throws Exception {
        // given
        AppUser user = getAppUser();

        String prefix = "[PRE]";
        given(emailConfiguration.getSubjectPrefix())
                .willReturn(prefix);
        String from = "test@rocketbase.io";
        given(emailConfiguration.getFromEmail())
                .willReturn(from);

        // when
        emailService.sentRegistrationEmail(user, "http://localhost:8080/");

        // then
        MimeMessage[] receivedMessages = getSmtpServerRule().getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];

        assertThat(current.getSubject(), startsWith(prefix + " "));
        assertThat(current.getFrom()[0].toString(), containsString(from));
        assertThat(current.getRecipients(Message.RecipientType.TO)[0].toString(), containsString(user.getEmail()));
        assertThat(current.getContent(), instanceOf(MimeMultipart.class));
        assertThat(((MimeMultipart) current.getContent()).getBodyPart(0).getSize(), greaterThan(0));
    }

    @Test
    public void buildActionUrlWithNullConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        given(emailConfiguration.getVerificationUrl())
                .willReturn(null);

        // when
        String result = emailService.buildActionUrl("user", applicationBaseUrl, ActionType.VERIFICATION);

        // then
        assertThat(result, startsWith(applicationBaseUrl + ActionType.VERIFICATION.getApiPath()));
    }

    @Test
    public void buildActionUrlWithFilledConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/";
        given(emailConfiguration.getVerificationUrl())
                .willReturn(configBaseUrl);

        // when
        String result = emailService.buildActionUrl("user", applicationBaseUrl, ActionType.VERIFICATION);

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?verification="));
    }

    @Test
    public void buildActionUrlWithFilledConfigQueryParam() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/?action=submit";
        given(emailConfiguration.getVerificationUrl())
                .willReturn(configBaseUrl);

        // when
        String result = emailService.buildActionUrl("user", applicationBaseUrl, ActionType.VERIFICATION);

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?action=submit&verification="));
    }
}