package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserEntity;

public interface MailContentConfig {

    HtmlTextEmail register(AppUserEntity user, String actionUrl);

    String registerSubject(AppUserEntity user);

    HtmlTextEmail forgotPassword(AppUserEntity user, String actionUrl);

    String forgotPasswordSubject(AppUserEntity user);

    HtmlTextEmail invite(AppInviteEntity invite, String actionUrl);

    String inviteSubject(AppInviteEntity invite);

}
