package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;

import javax.mail.internet.InternetAddress;

public interface EmailService {

    void sentRegistrationEmail(AppUserReference user, String verificationUrl);

    void sentForgotPasswordEmail(AppUserReference user, String verificationUrl);

    void sentInviteEmail(AppInviteEntity invite, String verificationUrl);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail, InternetAddress from);
}
