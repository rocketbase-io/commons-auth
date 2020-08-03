package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTestPrefixed;
import io.rocketbase.commons.config.RegistrationProperties;
import io.rocketbase.commons.dto.ExpirationInfo;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.registration.RegistrationRequest;
import io.rocketbase.commons.exception.AuthErrorCodes;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.RegistrationResource;
import io.rocketbase.commons.service.user.DefaultAppUserService;
import io.rocketbase.commons.test.AppUserPersistenceTestService;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RegistrationControllerTest extends BaseIntegrationTestPrefixed {

    @Resource
    private AppUserPersistenceTestService appUserPersistenceTestService;

    @Test
    public void registerSuccess() {
        // given
        RegistrationRequest registration = RegistrationRequest.builder()
                .username("new-user")
                .email("new-user@rocketbase.io")
                .password("r0cketB@se")
                .build();
        // when
        ExpirationInfo<AppUserRead> response = new RegistrationResource(getBaseUrl()).register(registration);

        // then
        assertThat(response, notNullValue());
        AppUserTestEntity appUser = appUserPersistenceTestService.findByUsername("new-user").get();
        assertThat(appUser.isEnabled(), equalTo(!new RegistrationProperties().isVerification()));
        assertThat(response.getExpires().isAfter(Instant.now()), equalTo(true));
        assertThat(appUser.getKeyValueMap().getOrDefault(DefaultAppUserService.REGISTRATION_KV, null), notNullValue());
    }

    @Test
    public void registerVerificationSuccess() {
        // given
        RegistrationRequest registration = RegistrationRequest.builder()
                .username("new-user")
                .email("new-user@rocketbase.io")
                .password("r0cketB@se")
                .build();
        RegistrationResource resource = new RegistrationResource(getBaseUrl());
        resource.register(registration);

        // when
        AppUserTestEntity appUser = appUserPersistenceTestService.findByUsername("new-user").get();
        String verification = appUser.getKeyValueMap().get(DefaultAppUserService.REGISTRATION_KV);
        JwtTokenBundle response = resource.verify(verification);
        // then
        assertThat(response, notNullValue());
    }

    @Test
    public void registerFailureUsername() {
        // given
        RegistrationRequest registration = RegistrationRequest.builder()
                .username("user")
                .email("new-user@rocketbase.io")
                .password("r0cketB@se")
                .build(); // when
        try {
            new RegistrationResource(getBaseUrl()).register(registration);
            // then
            Assert.fail("should have thrown RegistrationException");
        } catch (BadRequestException e) {
            assertThat(e.getErrorResponse().getStatus(), equalTo(AuthErrorCodes.REGISTRATION.getStatus()));
        }
    }

    @Test
    public void registerFailurePassword() {
        // given
        RegistrationRequest registration = RegistrationRequest.builder()
                .username("user")
                .email("new-user@rocketbase.io")
                .password("r0cket")
                .build(); // when
        try {
            new RegistrationResource(getBaseUrl()).register(registration);
            // then
            Assert.fail("should have thrown RegistrationException");
        } catch (BadRequestException e) {
            assertThat(e.getErrorResponse().getStatus(), equalTo(AuthErrorCodes.REGISTRATION.getStatus()));
        }
    }

}