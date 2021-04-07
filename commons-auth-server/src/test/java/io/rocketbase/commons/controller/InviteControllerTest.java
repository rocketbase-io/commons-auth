package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.ConfirmInviteRequest;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.model.AppInviteMongoEntity;
import io.rocketbase.commons.resource.AppInviteResource;
import io.rocketbase.commons.resource.InviteResource;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InviteControllerTest extends BaseIntegrationTest {

    @Resource
    private AppInvitePersistenceService<AppInviteEntity> appInvitePersistenceService;


    @Test
    public void verifyValid() {
        // given
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppInviteRead invited = appInviteResource.invite(AppInviteControllerTest.INVITE_REQUEST);

        // when
        InviteResource inviteResource = new InviteResource(getBaseUrl());
        AppInviteRead response = inviteResource.verify(invited.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.getInvitor(), equalTo(invited.getInvitor()));
        assertThat(response.getExpiration(), greaterThan(Instant.now()));
    }

    @Test
    public void verifyExpired() {
        // given
        AppInviteEntity expired = appInvitePersistenceService.save(AppInviteMongoEntity.builder()
                .id(123456789L)
                .email("expired@rocketbase.io")
                .expiration(Instant.now().minus(10, ChronoUnit.MINUTES))
                .build());

        // when
        InviteResource inviteResource = new InviteResource(getBaseUrl());
        try {
            inviteResource.verify(expired.getId());
        } catch (Exception e) {
            // then
            assertThat(e, instanceOf(BadRequestException.class));
            assertThat(((BadRequestException)e).getErrorResponse().getFields().keySet(), hasItem("inviteId"));
        }
    }

    @Test
    public void transformToUser() {
        // given
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppInviteRead invited = appInviteResource.invite(AppInviteControllerTest.INVITE_REQUEST);

        ConfirmInviteRequest confirm = ConfirmInviteRequest.builder()
                .inviteId(invited.getId())
                .username("invited.username")
                .email(invited.getEmail())
                .password("öälk12@3Lasd3bs")
                .build();

        // when
        InviteResource inviteResource = new InviteResource(getBaseUrl());
        AppUserRead response = inviteResource.transformToUser(confirm);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getEmail(), equalTo(invited.getEmail()));
        assertThat(response.getKeyValues(), equalTo(invited.getKeyValues()));
        assertThat(response.getGroups(), equalTo(response.getGroups()));
        assertThat(response.getCapabilities(), equalTo(response.getCapabilities()));
        assertThat(response.getUsername(), equalTo(confirm.getUsername()));
    }
}