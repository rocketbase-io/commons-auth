package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class AppUserJpaServiceImplTest {

    @Resource
    private AppUserPersistenceService<AppUserJpaEntity> service;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Before
    public void beforeEachTest() {
        service.save(AppUserJpaEntity.builder()
                .id("401fb225-057e-4e0a-a0ff-e99e76030d52")
                .username("marten")
                .firstName("Marten")
                .lastName("Prieß")
                .email("marten@rocketbase.io")
                .roles(Arrays.asList("ADMIN"))
                .password(passwordEncoder.encode("password"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").build())
                .enabled(true)
                .created(Instant.now())
                .build());
        service.save(AppUserJpaEntity.builder()
                .id("c3c58d60-e948-442f-9783-c0341c65a367")
                .username("niels")
                .firstName("Niels")
                .lastName("Schelbach")
                .email("niels@rocketbase.io")
                .roles(Arrays.asList("USER"))
                .password(passwordEncoder.encode("password"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").put("language", "en").build())
                .enabled(true)
                .created(Instant.now())
                .build());
        service.save(AppUserJpaEntity.builder()
                .id("d74678ea-6689-4c6f-a055-e275b4a2a61c")
                .username("sample")
                .firstName("Sample")
                .lastName("User")
                .email("sampled@rocketbase.io")
                .roles(Arrays.asList("user"))
                .password(passwordEncoder.encode("password"))
                .enabled(false)
                .created(Instant.now())
                .build());
        service.save(AppUserJpaEntity.builder()
                .id("f55e3176-3fca-4100-bb26-853106269fb1")
                .username("service")
                .firstName("Service")
                .email("servicee@rocketbase.io")
                .roles(Arrays.asList("service"))
                .password(passwordEncoder.encode("password"))
                .enabled(true)
                .created(Instant.now())
                .build());
    }

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

    @Test
    public void buildLikeString() {
        // given
        AppUserJpaServiceImpl appUserJpaService = new AppUserJpaServiceImpl(null);

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
                .keyValue("workspace", "1")
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
                .keyValue("workspace", "1")
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