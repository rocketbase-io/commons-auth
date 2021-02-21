package io.rocketbase.commons.service.email;

import com.google.common.collect.Sets;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.model.SimpleAppUserReference;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.model.SimpleAppInvite;
import org.junit.Test;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.data.util.Pair;

import javax.annotation.Resource;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class MailContentConfigTest extends BaseIntegrationTest {

    @Resource
    private MailContentConfig mailContentConfig;

    private Mailer mailer;

    protected Mailer getMailer() {
        if (mailer == null) {
            // default
            mailer = MailerBuilder.withSMTPServer("localhost", 1025)
                    .buildMailer();
        }
        return mailer;
    }

    protected void sentEmail(String subject, Pair<String, String> content) {
        try {
            Email email = EmailBuilder.startingBlank()
                    .to("melistik@icloud.com")
                    .from("service@rocketbase.io")
                    .withSubject(subject)
                    .withHTMLText(content.getFirst())
                    .withPlainText(content.getSecond())
                    .buildEmail();
            getMailer().sendMail(email);
        } catch (Exception e) {

            // ignore error here - works only on local machine for visible test-purpose
        }
    }

    @Test
    public void register() {
        // given
        String actionUrl = "http://localhost:8080/?action=register";
        AppUserReference appUserReference = SimpleAppUserReference.builder()
                .id("id")
                .username("username")
                .email("email@email.com")
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .build();

        // when
        Pair<String, String> htmlTextEmail = mailContentConfig.register(appUserReference, actionUrl);
        String subject = mailContentConfig.registerSubject(appUserReference);
        sentEmail(subject, htmlTextEmail);

        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(subject, notNullValue());
    }

    @Test
    public void forgotPassword() {
        // given
        String actionUrl = "http://localhost:8080/?action=forgot";
        AppUserReference appUserReference = SimpleAppUserReference.builder()
                .id("id")
                .username("username")
                .email("email@email.com")
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .build();

        // when
        Pair<String, String> htmlTextEmail = mailContentConfig.forgotPassword(appUserReference, actionUrl);
        String subject = mailContentConfig.forgotPasswordSubject(appUserReference);
        sentEmail(subject, htmlTextEmail);

        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(subject, notNullValue());
    }

    @Test
    public void invite() {
        // given
        String actionUrl = "http://localhost:8080/?action=forgot";
        AppInviteEntity inviteEntity = SimpleAppInvite.builder()
                .email("email@email.com")
                .firstName("firstName")
                .lastName("lastName")
                .created(Instant.now())
                .invitor("invitor")
                .message("My longer message from the other side\n" +
                        "Try also a linebreak :)")
                .capabilityIds(Sets.newHashSet(1L, 2L))
                .expiration(Instant.now().plusSeconds(600))
                .build();

        // when
        Pair<String, String> htmlTextEmail = mailContentConfig.invite(inviteEntity, actionUrl);
        String subject = mailContentConfig.inviteSubject(inviteEntity);
        sentEmail(subject, htmlTextEmail);

        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(subject, notNullValue());
    }


    @Test
    public void changeEmail() {
        // given
        String actionUrl = "http://localhost:8080/?action=change-mail";
        AppUserReference appUserReference = SimpleAppUserReference.builder()
                .id("id")
                .username("username")
                .email("email@email.com")
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .build();

        // when
        Pair<String, String> htmlTextEmail = mailContentConfig.changeEmail(appUserReference, "new-email@rocketbase.com", actionUrl);
        String subject = mailContentConfig.changeEmailSubject(appUserReference);
        sentEmail(subject, htmlTextEmail);

        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(subject, notNullValue());
    }

}