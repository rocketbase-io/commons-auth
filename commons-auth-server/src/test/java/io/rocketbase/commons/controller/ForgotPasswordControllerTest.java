package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.resource.ForgotPasswordResource;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.BaseUserIntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ForgotPasswordControllerTest extends BaseUserIntegrationTest {

    @Resource
    private AppUserService appUserService;

    @Test
    public void forgotPasswordByEmail() throws Exception {
        // given
        EmailProperties emailProperties = new EmailProperties();

        String email = "forget@rocketbase.io";
        ForgotPasswordResource forgotPasswordResource = new ForgotPasswordResource(getBaseUrl());
        appUserService.initializeUser("forget", "pw", email, false);

        // when
        forgotPasswordResource.forgotPassword(new ForgotPasswordRequest(null, email, null, null));

        // then
        MimeMessage[] receivedMessages = getSmtpServerRule().getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];
        assertThat(current.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(current.getFrom()[0].toString(), containsString(emailProperties.getFromEmail()));
        assertThat(current.getRecipients(Message.RecipientType.TO)[0].toString(), containsString(email));
    }

    @Test
    public void forgotPasswordByUsername() throws Exception {
        // given
        EmailProperties emailProperties = new EmailProperties();

        String username = "forgot-pw";
        String email = "forget@rocketbase.io";
        ForgotPasswordResource forgotPasswordResource = new ForgotPasswordResource(getBaseUrl());
        appUserService.initializeUser(username, "pw", email, false);

        // when
        forgotPasswordResource.forgotPassword(new ForgotPasswordRequest(username, null, null, null));

        // then
        MimeMessage[] receivedMessages = getSmtpServerRule().getMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage current = receivedMessages[0];
        assertThat(current.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(current.getFrom()[0].toString(), containsString(emailProperties.getFromEmail()));
        assertThat(current.getRecipients(Message.RecipientType.TO)[0].toString(), containsString(email));
    }

    @Test
    public void forgotPasswordFailure() throws Exception {
        // given
        ForgotPasswordResource forgotPasswordResource = new ForgotPasswordResource(getBaseUrl());

        // when
        try {
            forgotPasswordResource.forgotPassword(new ForgotPasswordRequest(null, "unkown@rocketbase.io", null, null));
            // then
            Assert.fail("should have thrown NotFoundException");
        } catch (Exception e) {
        }
    }
}