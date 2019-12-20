package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppUserJpaServiceImplTest {

    @Resource
    private AppUserPersistenceService<AppUserJpaEntity> service;

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
                .hasRole("uSeR")
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
                .hasRole("ADMIN")
                .build();

        // when
        Page<AppUserJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("marten"));
    }
}