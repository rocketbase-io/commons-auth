package io.rocketbase.commons.service.email;

import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppUserReference;
import org.springframework.data.util.Pair;

public interface MailContentConfig {

    /**
     * @param user      meta to use within email
     * @param actionUrl url for button to click
     * @return Pair with first html and second text
     */
    Pair<String, String> register(AppUserReference user, String actionUrl);

    String registerSubject(AppUserReference user);

    /**
     * @param user      meta to use within email
     * @param actionUrl url for button to click
     * @return Pair with first html and second text
     */
    Pair<String, String> forgotPassword(AppUserReference user, String actionUrl);

    String forgotPasswordSubject(AppUserReference user);

    /**
     * @param invite    meta to use within email
     * @param actionUrl url for button to click
     * @return Pair with first html and second text
     */
    Pair<String, String> invite(AppInviteEntity invite, String actionUrl);

    String inviteSubject(AppInviteEntity invite);

    /**
     * @param user            meta to use within email
     * @param newEmailAddress new email-address to use
     * @param actionUrl       url for button to click
     * @return Pair with first html and second text
     */
    Pair<String, String> changeEmail(AppUserReference user, String newEmailAddress, String actionUrl);

    String changeEmailSubject(AppUserReference user);

}
