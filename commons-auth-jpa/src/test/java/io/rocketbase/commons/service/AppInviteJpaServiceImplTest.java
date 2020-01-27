package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppInviteJpaServiceImplTest {

    @Resource
    private AppInvitePersistenceService<AppInviteJpaEntity> service;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppInvite query = null;

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppInvite query = QueryAppInvite.builder().build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(2L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("hello@rocketbase.io"));
        assertThat(result.getContent().get(1).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void findAllQueryExpired() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(true)
                .build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("expired@rocketbase.io"));
    }

    @Test
    public void findAllQuerySelected() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .invitor("MARTEN")
                .build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void findById() {
        // given

        // when
        Optional<AppInviteJpaEntity> result = service.findById("1314202d-e866-4452-b7fc-781e87d44c6c");

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void count() {
        // given

        // when
        long result = service.count();

        // then
        assertThat(result, equalTo(3L));
    }
}