package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;

public interface MailContentConfig {

    HtmlTextEmail register(AppUserReference user, String actionUrl);

    String registerSubject(AppUserReference user);

    HtmlTextEmail forgotPassword(AppUserReference user, String actionUrl);

    String forgotPasswordSubject(AppUserReference user);

    HtmlTextEmail invite(AppInviteEntity invite, String actionUrl);

    String inviteSubject(AppInviteEntity invite);

}
