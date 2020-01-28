package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteMongoEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppInviteMongoServiceImplTest {

    @Resource
    private AppInvitePersistenceService<AppInviteMongoEntity> service;

    @Before
    public void beforeEachTest() {
        service.deleteAll();

        service.save(AppInviteMongoEntity.builder()
                .id("1314202d-e866-4452-b7fc-781e87d44c6c")
                .created(Instant.now())
                .expiration(Instant.ofEpochSecond(1924988399))
                .invitor("Marten")
                .message("Please and join our Team from rocketbase.io")
                .email("valid@rocketbase.io")
                .roles(Arrays.asList("USER", "SERVICE"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").put("special", "abc").put("_secret", "secure").build())
                .build());
        service.save(AppInviteMongoEntity.builder()
                .id("d182397f-2a30-4006-afe8-a2d8ec427142")
                .created(Instant.now())
                .expiration(Instant.ofEpochSecond(1924988399))
                .invitor("Lukas")
                .message("...")
                .email("hello@rocketbase.io")
                .roles(Arrays.asList("SERVICE"))
                .build());
        service.save(AppInviteMongoEntity.builder()
                .id("3ac876a7-5156-499d-8f86-3b137e7fdcbc")
                .created(Instant.now().minus(5, ChronoUnit.DAYS))
                .expiration(Instant.now().minus(1, ChronoUnit.DAYS))
                .invitor("System Invalid")
                .message("Please and join our Team from rocketbase.io")
                .email("expired@rocketbase.io")
                .roles(Arrays.asList("USER", "SERVICE"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").build())
                .build());
    }

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppInvite query = null;

        // when
        Page<AppInviteMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppInvite query = QueryAppInvite.builder().build();

        // when
        Page<AppInviteMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

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
        Page<AppInviteMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

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
        Page<AppInviteMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void save() {
        // given
        AppInviteMongoEntity entity = service.initNewInstance();
        entity.setInvitor("Invitor");
        entity.setMessage("My little message");
        entity.setEmail("new@rocketbase.io");
        entity.setExpiration(Instant.now().plus(10, ChronoUnit.DAYS));
        entity.setRoles(Arrays.asList("USER", "SERVICE"));
        entity.addKeyValue("_secure", "geheim123");
        entity.addKeyValue("client", "abc");

        // when
        AppInviteMongoEntity result = service.save(entity);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getMessage(), equalTo(entity.getMessage()));
        assertThat(result.getEmail(), equalTo(entity.getEmail()));
        assertThat(result.getRoles(), equalTo(entity.getRoles()));
        assertThat(result.getExpiration(), equalTo(entity.getExpiration()));
    }

    @Test
    public void findById() {
        // given

        // when
        Optional<AppInviteMongoEntity> result = service.findById("1314202d-e866-4452-b7fc-781e87d44c6c");

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

    @Test
    public void delete() {
        // given

        // when
        service.delete(service.findById("1314202d-e866-4452-b7fc-781e87d44c6c").get());
        Optional<AppInviteMongoEntity> result = service.findById("1314202d-e866-4452-b7fc-781e87d44c6c");

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void deleteAll() {
        // given

        // when
        service.deleteAll();
        long result = service.count();

        // then
        assertThat(result, equalTo(0L));
    }

    @Test
    public void deleteExpired() {
        // given

        // when
        long result = service.deleteExpired();

        // then
        assertThat(result, equalTo(1L));
    }
}