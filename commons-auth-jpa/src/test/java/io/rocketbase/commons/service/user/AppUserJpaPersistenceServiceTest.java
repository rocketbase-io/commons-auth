package io.rocketbase.commons.service.user;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserJpaEntity;
import io.rocketbase.commons.service.JpaPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
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
public class AppUserJpaPersistenceServiceTest extends JpaPersistenceBaseTest {

    @Resource
    private AppUserPersistenceService<AppUserJpaEntity> service;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppUser query = null;

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(4L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppUser query = QueryAppUser.builder().build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
    }

    @Test
    public void findAllQueryFreetext() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .freetext("en")
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

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
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

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
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

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
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
    }

    @Test
    public void findAllQueryHasRole() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .capabilityIds(Sets.newHashSet(CapabilityData.USER_OBJECT.getId()))
                .enabled(true)
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("niels"));
    }

    @Test
    public void findAllQueryHasRoleAdmin() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .capabilityIds(Sets.newHashSet(CapabilityData.ROOT.getId()))
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("marten"));
    }

    @Test
    public void buildLikeString() {
        // given
        AppUserJpaPersistenceService appUserJpaService = new AppUserJpaPersistenceService(null, null, null, null);

        // when
        String simple = appUserJpaService.buildLikeString("simple");
        String beginning = appUserJpaService.buildLikeString("*sim");
        String ending = appUserJpaService.buildLikeString("sim*");
        String lower = appUserJpaService.buildLikeString("LOWER");

        // then
        assertThat(simple, equalTo("%simple%"));
        assertThat(beginning, equalTo("%sim"));
        assertThat(ending, equalTo("sim%"));
        assertThat(lower, equalTo("%lower%"));
    }


    @Test
    public void findAllWithKeyValue() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .enabled(true)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(2L));
        for (AppUserJpaEntity e : result.getContent()) {
            assertThat(e.getKeyValue("workspace"), equalTo("1"));
        }
    }

    @Test
    public void findAllWithKeyValueAndOtherFilters() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .enabled(true)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .freetext("ma")
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("marten"));
    }
}