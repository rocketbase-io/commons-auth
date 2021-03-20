package io.rocketbase.commons.service.user;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserMongoEntity;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import io.rocketbase.commons.service.MongoPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.GroupData;
import io.rocketbase.commons.test.data.UserData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Slf4j
public class AppUserMongoPersistenceServiceTest extends MongoPersistenceBaseTest {

    @Resource
    private AppUserPersistenceService<AppUserMongoEntity> service;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppUser query = null;

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(6L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppUser query = QueryAppUser.builder().build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(5L));
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
        assertThat(result.getContent().get(0).getUsername(), equalTo("user"));
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
        assertThat(result.getTotalElements(), equalTo(5L));
    }

    @Test
    public void findAllQueryWithCapability() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId()))
                .enabled(true)
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("service"));
    }

    @Test
    public void findAllQueryWithGroup() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .groupIds(Sets.newHashSet(GroupData.ADMIN_GROUP.getId()))
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("username")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getUsername(), equalTo("admin"));
    }


    @Test
    public void findAllKeyValues() {
        // given
        QueryAppUser query = QueryAppUser.builder()
                .keyValues(ImmutableMap.of("workspace", "1", "displayMode", "short"))
                .build();

        // when
        Page<AppUserMongoEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getKeyValue("workspace"), equalTo("1"));
        assertThat(result.getContent().get(0).getKeyValue("displayMode"), equalTo("short"));
    }


    @Test
    public void save() {
        // given

        AppUserMongoEntity entity = service.initNewInstance();
        entity.setSystemRefId("systemRefId");
        entity.setUsername("test123");
        entity.setPassword("44c84f48d51c687de2c66369dad6c525c2039ba9629eced9e943d8d9fc50e67a");
        entity.setEmail("test123@rocketbase.io");
        entity.setProfile(SimpleUserProfile.builder()
                .location("Hamburg")
                .salutation("Mrs")
                .gender(Gender.MALE)
                .firstName("firstName")
                .lastName("lastName")
                .build());
        entity.setSetting(SimpleUserSetting.builder()
                .currentTimeZone("Europe/Paris")
                .dateTimeFormat("yyyy-MM-dd HH:mm")
                .timeFormat("HH:mm")
                .dateFormat("yyyy-MM-dd")
                .locale("fr")
                .build());
        entity.setCapabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId(), CapabilityData.USER_WRITE.getId(), CapabilityData.API_ASSET.getId(), CapabilityData.API_BLOG_CRUD.getId()));
        entity.setGroupIds(Sets.newHashSet(GroupData.DEPARTMENT_GROUP.getId()));
        entity.setEnabled(true);
        entity.setLocked(false);
        entity.setLastLogin(Instant.now().plusSeconds(1000));
        entity.setLastTokenInvalidation(Instant.now().minusSeconds(1000));
        entity.setKeyValues(ImmutableMap.of("workspace", "123", "_secret", "--"));

        // when
        service.save(entity);
        AppUserMongoEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getCreated().truncatedTo(ChronoUnit.SECONDS)));
        assertThat(result.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(result.getUsername(), equalTo(entity.getUsername()));
        assertThat(result.getPassword(), equalTo(entity.getPassword()));
        assertThat(result.getEmail(), equalTo(entity.getEmail()));
        assertThat(result.getProfile(), equalTo(entity.getProfile()));
        assertThat(result.getSetting(), equalTo(entity.getSetting()));
        assertThat(result.getCapabilityIds(), equalTo(entity.getCapabilityIds()));
        assertThat(result.getGroupIds(), equalTo(entity.getGroupIds()));
        assertThat(result.isEnabled(), equalTo(entity.isEnabled()));
        assertThat(result.isLocked(), equalTo(entity.isLocked()));
        assertThat(result.getLastLogin().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getLastLogin().truncatedTo(ChronoUnit.SECONDS)));
        assertThat(result.getLastTokenInvalidation().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getLastTokenInvalidation().truncatedTo(ChronoUnit.SECONDS)));
        assertThat(result.getKeyValues(), equalTo(entity.getKeyValues()));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(UserData.NIELS.getId());
        Optional<AppUserMongoEntity> result = service.findById(UserData.NIELS.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(false));
    }
}