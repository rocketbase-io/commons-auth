package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appgroup.AppGroupRead;
import io.rocketbase.commons.dto.appgroup.AppGroupWrite;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupEntity;
import io.rocketbase.commons.resource.AppGroupResource;
import io.rocketbase.commons.service.group.AppGroupPersistenceService;
import io.rocketbase.commons.test.data.GroupData;
import io.rocketbase.commons.test.model.SimpleAppGroupEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppGroupControllerTest extends BaseIntegrationTest {

    @Resource
    private AppGroupPersistenceService<AppGroupEntity> appGroupPersistenceService;

    @Test
    public void find() {
        // given
        QueryAppGroup query = QueryAppGroup.builder()
                .description("department")
                .build();

        // when
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        PageableResult<AppGroupRead> response = resource.find(query, PageRequest.of(0, 100));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        SimpleAppGroupEntity object = GroupData.DEPARTMENT_TWO_GROUP;

        // when
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppGroupRead> response = resource.findById(object.getId());

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
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppGroupRead> response = resource.findById(0L);

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void create() {
        // given
        SimpleAppGroupEntity parent = GroupData.DEPARTMENT_GROUP;
        AppGroupWrite write = AppGroupWrite.builder()
                .description("new third subgroup")
                .name("three")
                .build();

        // when
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppGroupRead response = resource.create(parent.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getNamePath(), equalTo(parent.getNamePath()+"/"+write.getName()));

        Optional<AppGroupEntity> db = appGroupPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getParentId(), equalTo(parent.getId()));
    }

    @Test
    public void update() {
        // given
        SimpleAppGroupEntity object = GroupData.DEPARTMENT_TWO_GROUP;
        AppGroupWrite write = AppGroupWrite.builder()
                .description(object.getDescription())
                .name("2")
                .systemRefId(object.getSystemRefId())
                .keyValues(object.getKeyValues())
                .capabilityIds(object.getCapabilityIds())
                .build();
        String username = "admin";

        // when
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        AppGroupRead response = resource.update(object.getId(), write);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getDescription(), equalTo(write.getDescription()));
        assertThat(response.getName(), equalTo(write.getName()));
        assertThat(response.getNamePath(), equalTo(GroupData.DEPARTMENT_GROUP.getNamePath()+"/"+write.getName()));

        Optional<AppGroupEntity> db = appGroupPersistenceService.findById(response.getId());
        assertThat(db.isPresent(), equalTo(true));
        assertThat(db.get().getModified(), greaterThan(object.getModified()));
        assertThat(db.get().getModifiedBy(), equalTo(username));
    }

    @Test
    public void delete() {
        // given
        SimpleAppGroupEntity object = GroupData.DEPARTMENT_TWO_GROUP;

        // when
        AppGroupResource resource = new AppGroupResource(new JwtRestTemplate(getTokenProvider("admin")));
        resource.delete(object.getId());

        // then
        Optional<AppGroupEntity> db = appGroupPersistenceService.findById(object.getId());
        assertThat(db.isPresent(), equalTo(false));
    }
}