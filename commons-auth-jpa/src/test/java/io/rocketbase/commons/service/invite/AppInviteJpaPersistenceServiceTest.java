package io.rocketbase.commons.service.invite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteJpaEntity;
import io.rocketbase.commons.service.JpaPersistenceBaseTest;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.InviteData;
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
public class AppInviteJpaPersistenceServiceTest extends JpaPersistenceBaseTest {

    @Resource
    private AppInviteJpaPersistenceService service;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAppInvite query = null;

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(4L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppInvite query = QueryAppInvite.builder().build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(3L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("betty@rocketbase.io"));
        assertThat(result.getContent().get(1).getEmail(), equalTo("hello@rocketbase.io"));
        assertThat(result.getContent().get(2).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void findAllQueryExpired() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(true)
                .build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

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
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("hello@rocketbase.io"));
    }

    @Test
    public void save() {
        // given
        AppInviteJpaEntity entity = service.initNewInstance();
        entity.setInvitor("Invitor");
        entity.setMessage("My little message");
        entity.setEmail("new@rocketbase.io");
        entity.setExpiration(Instant.now().plus(10, ChronoUnit.DAYS));
        entity.setCapabilityIds(Sets.newHashSet(CapabilityData.API_ROOT.getId(), CapabilityData.USER_OBJECT.getId()));
        entity.addKeyValue("_secure", "geheim123");
        entity.addKeyValue("client", "abc");

        // when
        service.save(entity);
        AppInviteJpaEntity result = service.findById(entity.getId()).get();

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(entity.getId()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getModified(), notNullValue());
        assertThat(result.getModifiedBy(), notNullValue());
        assertThat(result.getMessage(), equalTo(entity.getMessage()));
        assertThat(result.getEmail(), equalTo(entity.getEmail()));
        assertThat(result.getCapabilityIds(), equalTo(entity.getCapabilityIds()));
        assertThat(result.getExpiration().truncatedTo(ChronoUnit.SECONDS), equalTo(entity.getExpiration().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    public void findById() {
        // given

        // when
        Optional<AppInviteJpaEntity> result = service.findById(InviteData.INVITE_ONE.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.isPresent(), equalTo(true));
    }

    @Test
    public void delete() {
        // given

        // when
        service.delete(InviteData.INVITE_TWO.getId());
        Optional<AppInviteJpaEntity> result = service.findById(InviteData.INVITE_TWO.getId());

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
    public void findAllKeyValues() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .keyValues(ImmutableMap.of("workspace", "1", "_secret","secure"))
                .build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getKeyValue("workspace"), equalTo("1"));
        assertThat(result.getContent().get(0).getKeyValue("_secret"), equalTo("secure"));
    }
}