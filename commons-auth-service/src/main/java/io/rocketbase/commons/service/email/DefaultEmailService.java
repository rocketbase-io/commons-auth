package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
        HtmlTextEmail htmlTextEmail = mailContentConfig.register(user, verificationUrl);

        sentEmail(new EmailAddress(user.getEmail()), mailContentConfig.registerSubject(user), htmlTextEmail);
    }

    @Override
    @SneakyThrows
    public void sentForgotPasswordEmail(AppUserReference user, String verificationUrl) {
        HtmlTextEmail htmlTextEmail = mailContentConfig.forgotPassword(user, verificationUrl);

        sentEmail(new EmailAddress(user.getEmail()), mailContentConfig.forgotPasswordSubject(user), htmlTextEmail);
    }

    @Override
    @SneakyThrows
    public void sentInviteEmail(AppInviteEntity invite, String verificationUrl) {
        HtmlTextEmail htmlTextEmail = mailContentConfig.invite(invite, verificationUrl);

        sentEmail(new EmailAddress(invite.getEmail()), mailContentConfig.inviteSubject(invite), htmlTextEmail);
    }

    public void sentEmail(EmailAddress to, String subject, HtmlTextEmail htmlTextEmail) {
        emailSender.sentEmail(to, subject, htmlTextEmail.getHtml(), htmlTextEmail.getText(), getFrom());
    }

}
