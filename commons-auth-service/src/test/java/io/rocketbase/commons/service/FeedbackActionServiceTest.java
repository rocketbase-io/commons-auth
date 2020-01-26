package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AuthProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class FeedbackActionServiceTest {

    @Test
    public void buildActionUrlWithNullConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(null);

        // when
        String result = new FeedbackSampleActionService(authProperties).buildActionUrl(applicationBaseUrl, FeedbackActionService.ActionType.VERIFICATION, "token", null);

        // then
        assertThat(result, startsWith(applicationBaseUrl + FeedbackActionService.ActionType.VERIFICATION.getApiPath()));
    }

    @Test
    public void buildActionUrlWithFilledConfig() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/";
        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(configBaseUrl);

        // when
        String result = new FeedbackSampleActionService(authProperties).buildActionUrl(applicationBaseUrl, FeedbackActionService.ActionType.VERIFICATION, "token", null);

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?verification="));
    }

    @Test
    public void buildActionUrlWithCustom() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/";
        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(configBaseUrl);

        // when
        String result = new FeedbackSampleActionService(authProperties).buildActionUrl(applicationBaseUrl, FeedbackActionService.ActionType.VERIFICATION, "token", "https://example.com/register");

        // then
        assertThat(result, startsWith("https://example.com/register?verification="));
    }

    @Test
    public void buildActionUrlWithFilledConfigQueryParam() {
        // given
        String applicationBaseUrl = "http://localhost:9090";

        String configBaseUrl = "https://api.rocketbase.io/?action=submit";
        AuthProperties authProperties = new AuthProperties();
        authProperties.setVerificationUrl(configBaseUrl);

        // when
        String result = new FeedbackSampleActionService(authProperties).buildActionUrl(applicationBaseUrl, FeedbackActionService.ActionType.VERIFICATION, "token", null);

        // then
        assertThat(result, startsWith("https://api.rocketbase.io/?action=submit&verification="));
    }

    @Getter
    @RequiredArgsConstructor
    private static class FeedbackSampleActionService implements FeedbackActionService {
        private final AuthProperties authProperties;
    }
}