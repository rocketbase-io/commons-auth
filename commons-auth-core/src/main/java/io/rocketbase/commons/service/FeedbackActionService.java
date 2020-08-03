package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AuthProperties;
import lombok.Getter;
import org.springframework.util.StringUtils;

public interface FeedbackActionService {

    AuthProperties getAuthProperties();

    default String buildActionUrl(String applicationBaseUrl, ActionType actionType, String token, String customUrl) {
        String result = StringUtils.isEmpty(customUrl) ? buildBaseUrl(applicationBaseUrl, actionType) : customUrl;

        result += result.contains("?") ? "&" : "?";
        result += actionType.getParameterName() + "=" + token;
        return result;
    }

    default String buildBaseUrl(String applicationBaseUrl, ActionType actionType) {
        String configuredUrl = null;
        switch (actionType) {
            case VERIFICATION_REGISTRATION:
                configuredUrl = getAuthProperties().getVerificationUrl();
                break;
            case PASSWORD_RESET:
                configuredUrl = getAuthProperties().getPasswordResetUrl();
                break;
            case INVITE:
                configuredUrl = getAuthProperties().getInviteUrl();
                break;
            case CHANGE_EMAIL:
                configuredUrl = getAuthProperties().getChangeEmailUrl();
                break;
            case CHANGE_USERNAME:
                configuredUrl = getAuthProperties().getChangeUsernameUrl();
                break;
        }

        String result;
        if (!StringUtils.isEmpty(configuredUrl)) {
            // in case of configured url this will have a full qualified url to a custom UI
            result = configuredUrl;
        } else {
            result = applicationBaseUrl;
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            result += actionType.getApiPath();
        }
        return result;
    }


    enum ActionType {

        VERIFICATION_REGISTRATION("/verification", "verification"),
        PASSWORD_RESET("/reset-password", "verification"),
        INVITE("/invite", "inviteId"),
        CHANGE_EMAIL("/change-email", "verification"),
        CHANGE_USERNAME("/change-username", "verification");

        @Getter
        private final String apiPath;

        @Getter
        private final String parameterName;

        ActionType(String apiPath, String parameterName) {
            this.apiPath = apiPath;
            this.parameterName = parameterName;
        }

    }
}
