package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppUser;

public interface MailContentConfig {

    TemplateConfigBuilder register(AppUser user, String actionUrl);

    String registerSubject(AppUser user);

    TemplateConfigBuilder forgotPassword(AppUser user, String actionUrl);

    String forgotPasswordSubject(AppUser user);

}
