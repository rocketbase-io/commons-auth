package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AuthProperties;
import lombok.Getter;
import org.springframework.util.StringUtils;

public interface FeedbackActionService {

    AuthProperties getAuthProperties();

    default String buildActionUrl(String applicationBaseUrl, ActionType actionType, String token, String customUrl) {
        String result = StringUtils.isEmpty(customUrl) ? buildBaseUrl(applicationBaseUrl, actionType) : customUrl;

        result += result.contains("?") ? "&" : "?";
        result += "verification=" + token;
        return result;
    }

    default String buildBaseUrl(String applicationBaseUrl, ActionType actionType) {
        String configuredUrl = null;
        switch (actionType) {
            case VERIFICATION:
                configuredUrl = getAuthProperties().getVerificationUrl();
                break;
            case PASSWORD_RESET:
                configuredUrl = getAuthProperties().getPasswordResetUrl();
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

        VERIFICATION("/verification"), PASSWORD_RESET("/reset-password");

        @Getter
        private String apiPath;

        ActionType(String apiPath) {
            this.apiPath = apiPath;
        }

    }
}
