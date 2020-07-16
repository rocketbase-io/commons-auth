package io.rocketbase.commons.service.email;

import io.rocketbase.commons.config.EmailProperties;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.EmailSenderTest;
import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EmailServiceTest extends BaseIntegrationTest {

    @Resource
    private AuthEmailService emailService;

    @Resource
    private EmailSenderTest emailSenderTest;

    @Test
    public void simpleSendRegistrationEmail() throws Exception {
        // given
        AppUserEntity user = getAppUser();

        EmailProperties emailProperties = new EmailProperties();

        // when
        emailService.sentRegistrationEmail(user, "http://localhost:8080/?verification=token");

        // then

        assertThat(emailSenderTest.getSubject(), startsWith(emailProperties.getSubjectPrefix() + " "));
        assertThat(emailSenderTest.getTo().getEmail(), containsString(user.getEmail()));
        assertThat(emailSenderTest.getHtml().length(), greaterThan(0));
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

        assertThat(emailSenderTest.getSubject(), is(String.format("%s Registrierung bestätigen", emailProperties.getSubjectPrefix())));
        assertThat(emailSenderTest.getText(), containsString("Sie Ihre Registrierung durch einen Klick auf den"));
    }
}