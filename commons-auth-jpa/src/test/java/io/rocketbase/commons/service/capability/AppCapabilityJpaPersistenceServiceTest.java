package io.rocketbase.commons.service.capability;

import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appcapability.QueryAppCapability;
import io.rocketbase.commons.model.AppCapabilityJpaEntity;
import io.rocketbase.commons.service.JpaPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppCapabilityJpaPersistenceServiceTest extends JpaPersistenceBaseTest {

    @Resource
    private AppCapabilityPersistenceService<AppCapabilityJpaEntity> service;

    @Test
    public void findById() {
        // given

        // when
        Optional<AppCapabilityJpaEntity> result = service.findById(CapabilityData.API_BLOG_PUBLISH.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void findAllById() {
        // given

        // when
        List<AppCapabilityJpaEntity> entities = service.findAllById(Arrays.asList(CapabilityData.API_BLOG_PUBLISH.getId(), CapabilityData.API_BLOG_CRUD.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(CapabilityData.API_BLOG_PUBLISH, CapabilityData.API_BLOG_CRUD));
    }

    @Test
    public void findAllByParentId() {
        // given

        // when
        List<AppCapabilityJpaEntity> entities = service.findAllByParentId(Arrays.asList(CapabilityData.API_ROOT.getId(), CapabilityData.USER_READ.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(CapabilityData.API_BLOG, CapabilityData.API_ASSET));
    }

    @Test
    public void findAllByKeyPath() {
        // given
        QueryAppCapability query = QueryAppCapability.builder()
                .keyPath("blog")
                .build();

        // when
        Page<AppCapabilityJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("keyPath")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result, contains(CapabilityData.API_BLOG, CapabilityData.API_BLOG_CRUD, CapabilityData.API_BLOG_PUBLISH));
    }

    @Test
    public void findAllByParentIds() {
        // given
        QueryAppCapability query = QueryAppCapability.builder()
                .parentIds(Sets.newHashSet(CapabilityData.API_BLOG.getId(), CapabilityData.USER_OBJECT.getId()))
                .build();

        // when
        Page<AppCapabilityJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("keyPath")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(4L));
        assertThat(result, contains(CapabilityData.API_BLOG_CRUD, CapabilityData.API_BLOG_PUBLISH, CapabilityData.USER_READ, CapabilityData.USER_WRITE));
    }

    @Test
    public void save() {
        // given
        AppCapabilityJpaEntity entity = service.initNewInstance();
        entity.setKey("customer");
        entity.setKeyPath("api.customer");
        entity.setParentId(CapabilityData.API_ROOT.getId());
        entity.setWithChildren(false);
        entity.setDescription("customer data access");

        // when
        service.save(entity);
        AppCapabilityJpaEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getModified(), notNullValue());
        assertThat(result.getModifiedBy(), notNullValue());
        assertThat(result.getKey(), equalTo(entity.getKey()));
        assertThat(result.getKeyPath(), equalTo(entity.getKeyPath()));
        assertThat(result.getParentId(), equalTo(entity.getParentId()));
        assertThat(result.isWithChildren(), equalTo(entity.isWithChildren()));
        assertThat(result.getDescription(), equalTo(entity.getDescription()));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(CapabilityData.API_BLOG_PUBLISH.getId());
        Optional<AppCapabilityJpaEntity> result = service.findById(CapabilityData.API_BLOG_PUBLISH.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void deleteTree() {
        // given

        // when
        service.delete(CapabilityData.API_ROOT.getId());
        Page<AppCapabilityJpaEntity> page = service.findAll(null, PageRequest.of(0, 10));

        // then
        assertThat(page, notNullValue());
        assertThat(page.getTotalElements(), equalTo(4L));
    }
}