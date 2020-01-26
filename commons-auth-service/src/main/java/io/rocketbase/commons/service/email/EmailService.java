package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppUserEntity;

import javax.mail.internet.InternetAddress;

public interface EmailService {

    void sentRegistrationEmail(AppUserEntity user, String verificationUrl);

    void sentForgotPasswordEmail(AppUserEntity user, String verificationUrl);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail, InternetAddress from);
}
