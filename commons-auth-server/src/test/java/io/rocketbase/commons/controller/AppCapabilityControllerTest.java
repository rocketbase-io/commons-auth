package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityRead;
import io.rocketbase.commons.dto.appcapability.AppCapabilityWrite;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityEntity;
import io.rocketbase.commons.resource.AppCapabilityResource;
import io.rocketbase.commons.service.capability.AppCapabilityPersistenceService;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.model.SimpleAppCapabilityEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppCapabilityControllerTest extends BaseIntegrationTest {

    @Resource
    private AppCapabilityPersistenceService<AppCapabilityEntity> appCapabilityPersistenceService;

    @Test
    public void find() {
        // given
        QueryAppCapability query = QueryAppCapability.builder()
                .key("l")
                .build();

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider("admin")));
        PageableResult<AppCapabilityRead> response = resource.find(query, PageRequest.of(0, 100));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        SimpleAppCapabilityEntity object = CapabilityData.API_BLOG_PUBLISH;

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppCapabilityRead> response = resource.findById(object.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getKey(), equalTo(object.getKey()));
        assertThat(response.get().getDescription(), equalTo(object.getDescription()));
    }

    @Test
    public void findByIdUnknown() {
        // given

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppCapabilityRead> response = resource.findById(0L);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void create() {
        // given
        SimpleAppCapabilityEntity parent = CapabilityData.API_ASSET;
        AppCapabilityWrite write = AppCapabilityWrite.builder()
                .description("description")
                .key("new")
                .build();

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppCapabilityRead response = resource.create(parent.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getKey(), equalTo(write.getKey()));

        Optional<AppCapabilityEntity> db = appCapabilityPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getParentId(), equalTo(parent.getId()));
    }

    @Test
    public void update() {
        // given
        SimpleAppCapabilityEntity object = CapabilityData.API_BLOG_CRUD;
        AppCapabilityWrite write = AppCapabilityWrite.builder()
                .description("new crud description")
                .key("create-read-update-delete")
                .build();
        String username = "admin";

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider(username)));
        AppCapabilityRead response = resource.update(object.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getKey(), equalTo(write.getKey()));

        Optional<AppCapabilityEntity> db = appCapabilityPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getModified(), greaterThan(object.getModified()));
        assertThat(db.get().getModifiedBy(), equalTo(username));
    }

    @Test
    public void delete() {
        // given
        SimpleAppCapabilityEntity object = CapabilityData.API_BLOG_PUBLISH;

        // when
        AppCapabilityResource resource = new AppCapabilityResource(new JwtRestTemplate(getTokenProvider("admin")));
        resource.delete(object.getId());

        // then
        Optional<AppCapabilityEntity> db = appCapabilityPersistenceService.findById(object.getId());
        assertThat(db.isPresent(), equalTo(false));
    }
}