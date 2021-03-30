package io.rocketbase.commons.service.client;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appclient.QueryAppClient;
import io.rocketbase.commons.model.AppClientMongoEntity;
import io.rocketbase.commons.service.MongoPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.ClientData;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppClientMongoPersistenceServiceTest extends MongoPersistenceBaseTest {

    @Resource
    private AppClientPersistenceService<AppClientMongoEntity> service;

    @Test
    public void findById() {
        // given

        // when
        Optional<AppClientMongoEntity> result = service.findById(ClientData.LOCALHOST.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void findAllById() {
        // given

        // when
        List<AppClientMongoEntity> entities = service.findAllById(Arrays.asList(ClientData.LOCALHOST.getId(), ClientData.EXAMPLE_BLOG.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(ClientData.LOCALHOST, ClientData.EXAMPLE_BLOG));
    }

    @Test
    public void findAllByCapabilityIds() {
        // given
        QueryAppClient query = QueryAppClient.builder()
                .capabilityIds(Sets.newHashSet(CapabilityData.API_BLOG.getId()))
                .build();

        // when
        Page<AppClientMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("name")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent(), contains(ClientData.EXAMPLE_BLOG));
    }

    @Test
    public void findAllByRedirectUrl() {
        // given
        QueryAppClient query = QueryAppClient.builder()
                .redirectUrl("localhost")
                .build();

        // when
        Page<AppClientMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("name")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result, contains(ClientData.LOCALHOST));
    }

    @Test
    public void save() {
        // given
        AppClientMongoEntity entity = service.initNewInstance();
        entity.setSystemRefId("systemRefId");
        entity.setName("name");
        entity.setDescription("description 123");
        entity.setCapabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId(), CapabilityData.API_ROOT.getId()));
        entity.setRedirectUrls(Sets.newHashSet("http://www.rocketbase.io", "https://www.rocketbase.io"));

        // when
        service.save(entity);
        AppClientMongoEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getCreated().truncatedTo(ChronoUnit.SECONDS)));
        assertThat(result.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(result.getName(), equalTo(entity.getName()));
        assertThat(result.getDescription(), equalTo(entity.getDescription()));
        assertThat(result.getCapabilityIds(), equalTo(entity.getCapabilityIds()));
        assertThat(result.getRedirectUrls(), equalTo(entity.getRedirectUrls()));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(ClientData.LOCALHOST.getId());
        Optional<AppClientMongoEntity> result = service.findById(ClientData.LOCALHOST.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }
}