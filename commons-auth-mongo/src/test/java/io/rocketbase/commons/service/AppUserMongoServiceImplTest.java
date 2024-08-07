package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AppUserMongoServiceImplTest extends BaseIntegrationTest {

    @Resource
    private AppUserPersistenceService<AppUserMongoEntity> service;

    @Before
    public void beforeEachTest() {
        service.save(AppUserMongoEntity.builder()
                .id("401fb225-057e-4e0a-a0ff-e99e76030d52")
                .username("marten")
                .firstName("Marten")
                .lastName("Prieß")
                .email("marten@rocketbase.io")
                .roles(Arrays.asList("ADMIN"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").build())
                .enabled(true)
                .build());
        service.save(AppUserMongoEntity.builder()
                .id("c3c58d60-e948-442f-9783-c0341c65a367")
                .username("niels")
                .firstName("Niels")
                .lastName("Schelbach")
                .email("niels@rocketbase.io")
                .roles(Arrays.asList("USER"))
                .keyValueMap(ImmutableMap.<String, String>builder().put("workspace", "1").put("language", "en").build())
                .enabled(true)
                .build());
        service.save(AppUserMongoEntity.builder()
                .id("d74678ea-6689-4c6f-a055-e275b4a2a61c")
                .username("sample")
                .firstName("Sample")
                .lastName("User")
                .email("sampled@rocketbase.io")
                .roles(Arrays.asList("user"))
                .enabled(false)
                .build());
        service.save(AppUserMongoEntity.builder()
                .id("f55e3176-3fca-4100-bb26-853106269fb1")
                .username("service")
                .firstName("Service")
                .email("servicee@rocketbase.io")
                .roles(Arrays.asList("service"))
                .enabled(true)
                .build());
    }


    @Test
    public void findAllNullQuery() {
        // given
        QueryAppUser query = null;

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(4L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppUser query = QueryAppUser.builder().build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

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
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

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
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("marten"));
    }


    @Test
    public void findAllKeyValues() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .keyValue("workspace", "1")
                .keyValue("language", "en")
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getKeyValue("workspace"), equalTo("1"));
        assertThat(result.getContent().get(0).getKeyValue("language"), equalTo("en"));
    }
}