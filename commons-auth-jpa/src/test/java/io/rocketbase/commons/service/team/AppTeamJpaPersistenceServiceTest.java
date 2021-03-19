package io.rocketbase.commons.service.team;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.QueryAppTeam;
import io.rocketbase.commons.model.AppTeamJpaEntity;
import io.rocketbase.commons.service.JpaPersistenceBaseTest;
import io.rocketbase.commons.test.data.TeamData;
import io.rocketbase.commons.test.data.UserData;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppTeamJpaPersistenceServiceTest extends JpaPersistenceBaseTest {

    @Resource
    private AppTeamPersistenceService<AppTeamJpaEntity> service;

    @Test
    public void findById() {
        // given

        // when
        Optional<AppTeamJpaEntity> result = service.findById(TeamData.TEAM_ONE.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void findAllById() {
        // given

        // when
        List<AppTeamJpaEntity> entities = service.findAllById(Arrays.asList(TeamData.TEAM_ONE.getId(), TeamData.TEAM_THREE.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(TeamData.TEAM_ONE, TeamData.TEAM_THREE));
    }

    @Test
    public void findAll() {
        // given
        QueryAppTeam query = QueryAppTeam.builder()
                .personal(false)
                .build();

        // when
        Page<AppTeamJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("name")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(2L));
        assertThat(result, contains(TeamData.TEAM_THREE, TeamData.TEAM_TWO));
    }

    @Test
    public void save() {
        // given
        AppTeamJpaEntity entity = service.initNewInstance();
        entity.setSystemRefId("systemRefId");
        entity.setName("name");
        entity.setDescription("description");
        entity.setPersonal(true);
        entity.setKeyValues(ImmutableMap.of("workspace", "333", "_secret", "topsecret"));
        entity.setMembers(ImmutableMap.of(UserData.NIELS.getId(), AppTeamRole.OWNER, UserData.LUISE.getId(), AppTeamRole.MEMBER));

        // when
        service.save(entity);
        AppTeamJpaEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getModified(), notNullValue());
        assertThat(result.getModifiedBy(), notNullValue());
        assertThat(result.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(result.getName(), equalTo(entity.getName()));
        assertThat(result.getDescription(), equalTo(entity.getDescription()));
        assertThat(result.isPersonal(), equalTo(entity.isPersonal()));
        assertThat(result.getKeyValues(), equalTo(entity.getKeyValues()));
        assertThat(result.getMembers(), equalTo(entity.getMembers()));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(TeamData.TEAM_THREE.getId());
        Optional<AppTeamJpaEntity> result = service.findById(TeamData.TEAM_THREE.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }
}