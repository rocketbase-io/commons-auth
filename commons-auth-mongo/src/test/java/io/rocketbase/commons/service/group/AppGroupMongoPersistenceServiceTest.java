package io.rocketbase.commons.service.group;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appgroup.QueryAppGroup;
import io.rocketbase.commons.model.AppGroupMongoEntity;
import io.rocketbase.commons.service.MongoPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.GroupData;
import org.junit.Test;
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

public class AppGroupMongoPersistenceServiceTest extends MongoPersistenceBaseTest {

    @Resource
    private AppGroupPersistenceService<AppGroupMongoEntity> service;

    @Test
    public void findById() {
        // given

        // when
        Optional<AppGroupMongoEntity> result = service.findById(GroupData.ADMIN_GROUP.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void findAllById() {
        // given

        // when
        List<AppGroupMongoEntity> entities = service.findAllById(Arrays.asList(GroupData.ADMIN_GROUP.getId(), GroupData.DEPARTMENT_ONE_GROUP.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(GroupData.ADMIN_GROUP, GroupData.DEPARTMENT_ONE_GROUP));
    }

    @Test
    public void findAllByParentId() {
        // given

        // when
        List<AppGroupMongoEntity> entities = service.findAllByParentId(Arrays.asList(GroupData.ADMIN_GROUP.getId(), GroupData.DEPARTMENT_GROUP.getId()));

        // then
        assertThat(entities, notNullValue());
        assertThat(entities, containsInAnyOrder(GroupData.DEPARTMENT_ONE_GROUP, GroupData.DEPARTMENT_TWO_GROUP));
    }

    @Test
    public void findAllByNamePath() {
        // given
        QueryAppGroup query = QueryAppGroup.builder()
                .namePath("department")
                .build();

        // when
        Page<AppGroupMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("namePath")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result, contains(GroupData.DEPARTMENT_GROUP,GroupData.DEPARTMENT_ONE_GROUP, GroupData.DEPARTMENT_TWO_GROUP));
    }

    @Test
    public void findAllByParentIds() {
        // given
        QueryAppGroup query = QueryAppGroup.builder()
                .parentIds(Sets.newHashSet(GroupData.DEPARTMENT_GROUP.getId(), GroupData.ADMIN_GROUP.getId()))
                .build();

        // when
        Page<AppGroupMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("namePath")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(2L));
        assertThat(result, contains(GroupData.DEPARTMENT_ONE_GROUP, GroupData.DEPARTMENT_TWO_GROUP));
    }

    @Test
    public void save() {
        // given
        AppGroupMongoEntity entity = service.initNewInstance();
        entity.setSystemRefId("systemRefId");
        entity.setName("manager");
        entity.setParentId(GroupData.ROOT.getId());
        entity.setWithChildren(false);
        entity.setDescription("manager data access");
        entity.setCapabilityIds(Sets.newHashSet(CapabilityData.USER_OBJECT.getId(), CapabilityData.API_ROOT.getId()));
        entity.setKeyValues(ImmutableMap.of("displayMode", "detailed"));

        // when
        service.save(entity);
        AppGroupMongoEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getCreated().truncatedTo(ChronoUnit.SECONDS)));
        assertThat(result.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(result.getName(), equalTo(entity.getName()));
        assertThat(result.getParentId(), equalTo(entity.getParentId()));
        assertThat(result.isWithChildren(), equalTo(entity.isWithChildren()));
        assertThat(result.getDescription(), equalTo(entity.getDescription()));
        assertThat(result.getCapabilityIds(), equalTo(entity.getCapabilityIds()));
        assertThat(result.getKeyValues(), equalTo(entity.getKeyValues()));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(GroupData.DEPARTMENT_TWO_GROUP.getId());
        Optional<AppGroupMongoEntity> result = service.findById(GroupData.DEPARTMENT_TWO_GROUP.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void deleteTree() {
        // given

        // when
        service.delete(GroupData.DEPARTMENT_GROUP.getId());
        Page<AppGroupMongoEntity> page = service.findAll(null, PageRequest.of(0, 10));

        // then
        assertThat(page, notNullValue());
        assertThat(page.getTotalElements(), equalTo(2L));
    }
}