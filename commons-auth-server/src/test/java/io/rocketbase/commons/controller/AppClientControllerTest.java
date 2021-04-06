package io.rocketbase.commons.controller;

import com.google.common.collect.Sets;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appclient.AppClientRead;
import io.rocketbase.commons.dto.appclient.AppClientWrite;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientEntity;
import io.rocketbase.commons.resource.AppClientResource;
import io.rocketbase.commons.service.client.AppClientPersistenceService;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.ClientData;
import io.rocketbase.commons.test.model.SimpleAppClientEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppClientControllerTest extends BaseIntegrationTest {

    @Resource
    private AppClientPersistenceService<AppClientEntity> appClientPersistenceService;

    @Test
    public void find() {
        // given
        QueryAppClient query = QueryAppClient.builder()
                .description("website")
                .build();

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        PageableResult<AppClientRead> response = resource.find(query, PageRequest.of(0, 100));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        SimpleAppClientEntity object = ClientData.EXAMPLE_BLOG;

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppClientRead> response = resource.findById(object.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getName(), equalTo(object.getName()));
        assertThat(response.get().getDescription(), equalTo(object.getDescription()));
    }

    @Test
    public void findByIdUnknown() {
        // given

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppClientRead> response = resource.findById(0L);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void create() {
        // given
        AppClientWrite write = AppClientWrite.builder()
                .name("test")
                .description("description")
                .capabilityIds(Sets.newHashSet(CapabilityData.USER_WRITE.getId(), CapabilityData.API_ASSET.getId()))
                .redirectUrls(Sets.newHashSet("http://localhost:3000"))
                .build();

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppClientRead response = resource.create(write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getRedirectUrls(), equalTo(write.getRedirectUrls()));
        assertThat(response.getCapabilities().stream().map(AppCapabilityShort::getId).collect(Collectors.toSet()), equalTo(write.getRedirectUrls()));
    }

    @Test
    public void update() {
        // given
        SimpleAppClientEntity object = ClientData.EXAMPLE_BLOG;
        AppClientWrite write = AppClientWrite.builder()
                .name("example-abc")
                .description("website example blog...")
                .capabilityIds(object.getCapabilityIds())
                .redirectUrls(object.getRedirectUrls())
                .build();
        String username = "admin";

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppClientRead response = resource.update(object.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getRedirectUrls(), equalTo(write.getRedirectUrls()));
        assertThat(response.getCapabilities().stream().map(AppCapabilityShort::getId).collect(Collectors.toSet()), equalTo(write.getRedirectUrls()));

        Optional<AppClientEntity> db = appClientPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getModified(), greaterThan(object.getModified()));
        assertThat(db.get().getModifiedBy(), equalTo(username));
    }

    @Test
    public void delete() {
        // given
        SimpleAppClientEntity object = ClientData.EXAMPLE_BLOG;

        // when
        AppClientResource resource = new AppClientResource(new JwtRestTemplate(getTokenProvider("admin")));
        resource.delete(object.getId());

        // then
        Optional<AppClientEntity> db = appClientPersistenceService.findById(object.getId());
        assertThat(db.isPresent(), equalTo(false));
    }
}