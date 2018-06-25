package io.rocketbase.commons.service.email;

import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.model.AppUser;

public interface MailContentConfig {

    HtmlTextEmail register(AppUser user, String actionUrl);

    String registerSubject(AppUser user);

    HtmlTextEmail forgotPassword(AppUser user, String actionUrl);

    String forgotPasswordSubject(AppUser user);

}
