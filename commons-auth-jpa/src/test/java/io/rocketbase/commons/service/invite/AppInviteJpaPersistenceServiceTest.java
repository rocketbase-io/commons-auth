package io.rocketbase.commons.service.invite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.dto.appinvite.QueryAppInvite;
import io.rocketbase.commons.model.AppInviteEntity;
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
        assertThat(result.getTotalElements(), equalTo(3L));
    }


    @Test
    public void findAllEmptyQuery() {
        // given
        QueryAppInvite query = QueryAppInvite.builder().build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(2L));
        assertThat(result.getContent().get(0).getEmail(), equalTo("hello@rocketbase.io"));
        assertThat(result.getContent().get(1).getEmail(), equalTo("valid@rocketbase.io"));
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
        assertThat(result.getContent().get(0).getEmail(), equalTo("valid@rocketbase.io"));
    }

    @Test
    public void save() {
        // given
        AppInviteJpaEntity entity = AppInviteJpaEntity.builder()
                .invitor("Invitor")
                .message("My little message")
                .email("new@rocketbase.io")
                .expiration(Instant.now().plus(10, ChronoUnit.DAYS))
                .capabilityHolder(Sets.newHashSet(CapabilityData.USER_READ.getId(), CapabilityData.API_ROOT.getId()))
                .keyValues(ImmutableMap.of("_secure", "geheim123", "client", "abc"))
                .build();

        // when
        AppInviteJpaEntity result = service.save(entity);

        // then
        assertThat(result, notNullValue());
        assertThat(result.getMessage(), equalTo(entity.getMessage()));
        assertThat(result.getEmail(), equalTo(entity.getEmail()));
        assertThat(result.getCapabilities(), equalTo(entity.getCapabilities()));
        assertThat(result.getExpiration(), equalTo(entity.getExpiration()));
        assertThat(result.getKeyValue("_secure"), equalTo("geheim123"));
        assertThat(result.hasKeyValue("client"), equalTo("abc"));
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
        service.delete(InviteData.INVITE_ONE.getId());
        Optional<AppInviteJpaEntity> result = service.findById(InviteData.INVITE_ONE.getId());

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
    public void findAllWithKeyValue() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(false)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .build();
        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        for (AppInviteEntity e : result.getContent()) {
            assertThat(e.getKeyValue("workspace"), equalTo("1"));
            if (e.getExpiration()  != null && e.getExpiration().isBefore(Instant.now())) {
                assertThat("expired", false);
            }
        }
    }

    @Test
    public void findAllWithKeyValueAndOtherFilters() {
        // given
        QueryAppInvite query = QueryAppInvite.builder()
                .expired(false)
                .keyValues(ImmutableMap.of("workspace", "1"))
                .invitor("ma")
                .build();

        // when
        Page<AppInviteJpaEntity> result = service.findAll(query, PageRequest.of(0, 10, Sort.by("email")));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
    }
}