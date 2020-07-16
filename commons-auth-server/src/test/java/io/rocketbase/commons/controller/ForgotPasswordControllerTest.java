package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTestPrefixed;
import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.dto.forgot.ForgotPasswordRequest;
import io.rocketbase.commons.resource.ForgotPasswordResource;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.EmailSenderTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

public class ForgotPasswordControllerTest extends BaseIntegrationTestPrefixed {

    @Resource
    private AppUserService appUserService;

    @Resource
    private EmailSenderTest emailSenderTest;

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
        assertThat(emailSenderTest.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(emailSenderTest.getFrom().getEmail(), containsString(emailProperties.getFromEmail()));
        assertThat(emailSenderTest.getTo().getEmail(), containsString(email));
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
        assertThat(emailSenderTest.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(emailSenderTest.getFrom().getEmail(), containsString(emailProperties.getFromEmail()));
        assertThat(emailSenderTest.getTo().getEmail(), containsString(email));
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