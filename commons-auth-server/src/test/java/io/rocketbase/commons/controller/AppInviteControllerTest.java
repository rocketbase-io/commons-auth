package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appinvite.AppInviteRead;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.resource.AppInviteResource;
import io.rocketbase.commons.test.data.InviteData;
import io.rocketbase.commons.test.model.SimpleAppInviteEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppInviteControllerTest extends BaseIntegrationTest {

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
        assertThat(response.getTotalElements(), greaterThan(2L));
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
}