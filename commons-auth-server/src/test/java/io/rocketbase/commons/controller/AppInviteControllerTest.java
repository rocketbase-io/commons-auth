package io.rocketbase.commons.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.InviteRequest;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.dto.appteam.AppTeamInvite;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.model.AppInviteEntity;
import io.rocketbase.commons.resource.AppInviteResource;
import io.rocketbase.commons.service.invite.AppInvitePersistenceService;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.GroupData;
import io.rocketbase.commons.test.data.InviteData;
import io.rocketbase.commons.test.data.TeamData;
import io.rocketbase.commons.test.model.SimpleAppInviteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppInviteControllerTest extends BaseIntegrationTest {

    @Resource
    private AppInvitePersistenceService<AppInviteEntity> appInvitePersistenceService;

    public final static InviteRequest INVITE_REQUEST = InviteRequest.builder()
            .invitor("junit-test :)")
            .message("please join us\nit's a pleasure to work with this tool...\ncheers...")
            .email("welcome@rocketbase.io")
            .capabilityIds(Sets.newHashSet(CapabilityData.API_ROOT.getId()))
            .teamInvite(new AppTeamInvite(TeamData.TEAM_TWO.getId(), AppTeamRole.MEMBER))
            .groupIds(Sets.newHashSet(GroupData.DEPARTMENT_ONE_GROUP.getId(), GroupData.DEPARTMENT_TWO_GROUP.getId()))
            .keyValues(ImmutableMap.of("k", "v"))
            .build();

    @Test
    public void find() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .invitor("ma")
                .build();

        // when
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        PageableResult<AppInviteRead> response = appInviteResource.find(query, PageRequest.of(0, 100));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), equalTo(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        SimpleAppInviteEntity invite = InviteData.INVITE_ONE;

        // when
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppInviteRead> response = appInviteResource.findById(invite.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getEmail(), equalTo(invite.getEmail()));
    }

    @Test
    public void findByIdUnknown() {
        // given

        // when
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppInviteRead> response = appInviteResource.findById(0L);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void invite() {
        // given
        InviteRequest inviteRequest = INVITE_REQUEST;

        // when
        AppInviteResource appInviteResource = new AppInviteResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppInviteRead response = appInviteResource.invite(inviteRequest);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getInvitor(), equalTo(inviteRequest.getInvitor()));
        assertThat(response.getMessage(), equalTo(inviteRequest.getMessage()));
        assertThat(response.getEmail(), equalTo(inviteRequest.getEmail()));

        AppInviteEntity db = appInvitePersistenceService.findById(response.getId()).get();
        assertThat(db.getExpiration(), greaterThan(Instant.now()));
        assertThat(db.getCapabilityIds(), equalTo(inviteRequest.getCapabilityIds()));
        assertThat(db.getGroupIds(), equalTo(inviteRequest.getGroupIds()));
        assertThat(db.getKeyValues(), equalTo(inviteRequest.getKeyValues()));
    }
}