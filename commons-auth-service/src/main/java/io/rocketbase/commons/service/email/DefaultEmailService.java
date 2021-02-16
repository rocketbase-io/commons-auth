package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import javax.annotation.Resource;

@Slf4j
@RequiredArgsConstructor
public class DefaultEmailService implements AuthEmailService {

    @Getter
    final EmailAddress from;

    @Resource
    private MailContentConfig mailContentConfig;

    @Resource
    private EmailSender emailSender;

    @Override
    @SneakyThrows
    public void sentRegistrationEmail(AppUserReference user, String verificationUrl) {
        Pair<String, String> content = mailContentConfig.register(user, verificationUrl);
        sentEmail(new EmailAddress(user.getEmail()), mailContentConfig.registerSubject(user), content);
    }

    @Override
    @SneakyThrows
    public void sentForgotPasswordEmail(AppUserReference user, String verificationUrl) {
        Pair<String, String> content = mailContentConfig.forgotPassword(user, verificationUrl);
        sentEmail(new EmailAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), content);
    }

    @Override
    @SneakyThrows
    public void sentInviteEmail(AppInviteEntity invite, String verificationUrl) {
        Pair<String, String> content = mailContentConfig.invite(invite, verificationUrl);
        sentEmail(new EmailAddress(invite.getEmail()), mailContentConfig.inviteSubject(invite), content);
    }

    @Override
    public void sentChangeEmailAddressEmail(AppUserReference user, String newEmailAddress, String verificationUrl) {
        Pair<String, String> content = mailContentConfig.changeEmail(user, newEmailAddress, verificationUrl);
        sentEmail(new EmailAddress(newEmailAddress), mailContentConfig.changeEmailSubject(user), content);
    }

    public void sentEmail(EmailAddress to, String subject, Pair<String, String> content) {
        emailSender.sentEmail(to, subject, content.getFirst(), content.getSecond(), getFrom());
    }

}
