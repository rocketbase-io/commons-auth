package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppUser;

import javax.mail.internet.InternetAddress;

public interface EmailService {

    void sentRegistrationEmail(AppUser user, String verificationUrl);

    void sentForgotPasswordEmail(AppUser user, String verificationUrl);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail);

    void sentEmail(InternetAddress to, String subject, HtmlTextEmail htmlTextEmail, InternetAddress from);
}
