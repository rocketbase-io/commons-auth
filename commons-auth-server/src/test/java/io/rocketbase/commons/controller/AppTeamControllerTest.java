package io.rocketbase.commons.controller;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appteam.AppTeamRead;
import io.rocketbase.commons.dto.appteam.AppTeamWrite;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamEntity;
import io.rocketbase.commons.resource.AppTeamResource;
import io.rocketbase.commons.service.team.AppTeamPersistenceService;
import io.rocketbase.commons.test.data.ClientData;
import io.rocketbase.commons.test.data.TeamData;
import io.rocketbase.commons.test.model.SimpleAppClientEntity;
import io.rocketbase.commons.test.model.SimpleAppTeamEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppTeamControllerTest extends BaseIntegrationTest {

    @Resource
    private AppTeamPersistenceService<AppTeamEntity> appTeamPersistenceService;

    @Test
    public void find() {
        // given
        QueryAppTeam query = QueryAppTeam.builder()
                .description("one")
                .build();

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
        PageableResult<AppTeamRead> response = resource.find(query, PageRequest.of(0, 100));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        SimpleAppClientEntity object = ClientData.EXAMPLE_BLOG;

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppTeamRead> response = resource.findById(object.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getName(), equalTo(object.getName()));
        assertThat(response.get().getDescription(), equalTo(object.getDescription()));
    }

    @Test
    public void findByIdUnknown() {
        // given

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppTeamRead> response = resource.findById(0L);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void create() {
        // given
        AppTeamWrite write = AppTeamWrite.builder()
                .name("test")
                .description("description")
                .systemRefId("sysref-123")
                .keyValues(ImmutableMap.of("k","1", "v", "bcd"))
                .personal(true)
                .build();

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppTeamRead response = resource.create(write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getSystemRefId(), equalTo(write.getSystemRefId()));
        assertThat(response.getKeyValues(), equalTo(write.getKeyValues()));
        assertThat(response.isPersonal(), equalTo(write.isPersonal()));
    }

    @Test
    public void update() {
        // given
        SimpleAppTeamEntity object = TeamData.TEAM_TWO;
        AppTeamWrite write = AppTeamWrite.builder()
                .name("2")
                .description(object.getDescription())
                .keyValues(object.getKeyValues())
                .personal(object.isPersonal())
                .build();
        String username = "admin";

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppTeamRead response = resource.update(object.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getKeyValues(), equalTo(write.getKeyValues()));
        assertThat(response.isPersonal(), equalTo(write.isPersonal()));

        Optional<AppTeamEntity> db = appTeamPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getModified(), greaterThan(object.getModified()));
        assertThat(db.get().getModifiedBy(), equalTo(username));
    }

    @Test
    public void delete() {
        // given
        SimpleAppTeamEntity object = TeamData.TEAM_TWO;

        // when
        AppTeamResource resource = new AppTeamResource(new JwtRestTemplate(getTokenProvider("admin")));
               resource.delete(object.getId());

        // then
        Optional<AppTeamEntity> db = appTeamPersistenceService.findById(object.getId());
        assertThat(db.isPresent(), equalTo(false));
    }
}