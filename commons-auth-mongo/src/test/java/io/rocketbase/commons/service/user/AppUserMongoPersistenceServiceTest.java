package io.rocketbase.commons.service.user;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.service.MongoPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.GroupData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Slf4j
public class AppUserMongoPersistenceServiceTest extends MongoPersistenceBaseTest {

    @Resource
    private AppUserPersistenceService<AppUserMongoEntity> service;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppUser query = null;

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(6L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppUser query = QueryAppUser.builder().build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(5L));
    }

    @Test
    public void findAllQueryFreetext() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .freetext("en")
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("marten"));
    }

    @Test
    public void findAllQuerySelected() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .username("s")
                .lastName("S")
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("niels"));
    }

    @Test
    public void findAllQuerySelectedWithEnabled() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .username("s")
                .lastName("S")
                .enabled(false)
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("sample"));
    }

    @Test
    public void findAllQueryEnabled() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .enabled(true)
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(5L));
    }

    @Test
    public void findAllQueryWithCapability() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId()))
                .enabled(true)
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("service"));
    }

    @Test
    public void findAllQueryWithGroup() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .groupIds(Sets.newHashSet(GroupData.ADMIN_GROUP.getId()))
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("admin"));
    }


    @Test
    public void findAllKeyValues() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .keyValues(ImmutableMap.of("workspace", "1", "displayMode", "short"))
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getKeyValue("workspace"), equalTo("1"));
        assertThat(result.getContent().get(0).getKeyValue("displayMode"), equalTo("short"));
    }
}