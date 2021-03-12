package io.rocketbase.commons.service.invite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import io.rocketbase.commons.test.model.SimpleAppInviteEntity;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppInviteJpaPersistenceServiceTest {

    @Resource
    private AppInviteJpaPersistenceService service;

    private static final List<Long> ids = Arrays.asList(1688465485600660000L, 1688465485600770000L, 1688465485600880000L);

    @Before
    public void beforeEachTest() {
        service.deleteAll();

        service.saveDto(SimpleAppInviteEntity.builder()
                .id(ids.get(0))
                .created(Instant.now())
                .expiration(Instant.ofEpochSecond(1924988399))
                .invitor("Marten")
                .message("Please and join our Team from rocketbase.io")
                .email("valid@rocketbase.io")
                .capabilities(Sets.newHashSet("USER", "SERVICE"))
                .keyValue("workspace", "1")
                .keyValue("special", "abc")
                .keyValue("_secret", "secure")
                .build());
        service.saveDto(SimpleAppInviteEntity.builder()
                .id(ids.get(1))
                .created(Instant.now())
                .expiration(Instant.ofEpochSecond(1924988399))
                .invitor("Lukas")
                .message("...")
                .email("hello@rocketbase.io")
                .capabilities(Sets.newHashSet("SERVICE"))
                .build());
        service.saveDto(SimpleAppInviteEntity.builder()
                .id(ids.get(3))
                .created(Instant.now().minus(5, ChronoUnit.DAYS))
                .expiration(Instant.now().minus(1, ChronoUnit.DAYS))
                .invitor("System Invalid")
                .message("Please and join our Team from rocketbase.io")
                .email("expired@rocketbase.io")
                .capabilities(Sets.newHashSet("USER", "SERVICE"))
                .keyValue("workspace", "1")
                .build());
    }

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppInvite query = null;

        // when
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppInvite query = QueryAppInvite.builder().build();

        // when
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

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
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10));

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
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void save() {
        // given
        AppInviteEntity entity = SimpleAppInviteEntity.builder()
                .invitor("Invitor")
                .message("My little message")
                .email("new@rocketbase.io")
                .expiration(Instant.now().plus(10, ChronoUnit.DAYS))
                .capabilities(Sets.newHashSet("USER", "SERVICE"))
                .keyValue("_secure", "geheim123")
                .keyValue("client", "abc")
                .build();

        // when
        AppInviteEntity result = service.saveDto(entity);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getMessage(), equalTo(entity.getMessage()));
        assertThat(result.getEmail(), equalTo(entity.getEmail()));
        assertThat(result.getCapabilities(), equalTo(entity.getCapabilities()));
        assertThat(result.getExpiration(), equalTo(entity.getExpiration()));
        assertThat(result.getKeyValue("_secure"), equalTo("geheim123"));
        assertThat(result.hasKeyValue("client"), equalTo("abc"));
    }

    @Test
    public void findById() {
        // given

        // when
        Optional<AppInviteJpaEntity> result = service.findById(ids.get(0));

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(ids.get(0));
        Optional<AppInviteJpaEntity> result = service.findById(ids.get(0));

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void deleteExpired() {
        // given

        // when
        long result = service.deleteExpired();

        // then
        assertThat(result, equalTo(1L));
    }

    @Test
    public void findAllWithKeyValue() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(false)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .build();
        // when
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        for (AppInviteEntity e : result.getContent()) {
            assertThat(e.getKeyValue("workspace"), equalTo("1"));
            if (e.getExpiration()  != null && e.getExpiration().isBefore(Instant.now())) {
                assertThat("expired", false);
            }
        }
    }

    @Test
    public void findAllWithKeyValueAndOtherFilters() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(false)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .invitor("ma")
                .build();

        // when
        Page<AppInviteEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
    }
}