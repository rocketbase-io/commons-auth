package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.mail.model.HtmlTextEmail;

public interface MailContentConfig {

    HtmlTextEmail register(AppUserReference user, String actionUrl);

    String registerSubject(AppUserReference user);

    HtmlTextEmail forgotPassword(AppUserReference user, String actionUrl);

    String forgotPasswordSubject(AppUserReference user);

    HtmlTextEmail invite(AppInviteEntity invite, String actionUrl);

    String inviteSubject(AppInviteEntity invite);

}
