package io.rocketbase.commons.controller;

import com.google.common.collect.Sets;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appcapability.AppCapabilityShort;
import io.rocketbase.commons.dto.appuser.*;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.resource.AppUserResource;
import io.rocketbase.commons.service.user.AppUserPersistenceService;
import io.rocketbase.commons.test.data.CapabilityData;
import io.rocketbase.commons.test.data.UserData;
import io.rocketbase.commons.util.Nulls;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppUserControllerTest extends BaseIntegrationTest {

    @Resource
    private AppUserPersistenceService<AppUserEntity> appUserPersistenceService;

    @Test
    public void find() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        PageableResult<AppUserRead> response = appUserResource.find(0, 100);

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(100));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByIdKnown() {
        // given
        AppUserEntity user = getAppUser("user");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppUserRead> response = appUserResource.findById(user.getId());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void findByIdUnknown() {
        // given

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(getTokenProvider("admin")));
        Optional<AppUserRead> response = appUserResource.findById("0unkown");

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }

    @Test
    public void findQuery() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        PageableResult<AppUserRead> response = appUserResource.find(QueryAppUser.builder().email("M").build(), PageRequest.of(0, 10));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(10));
        assertThat(response.getTotalElements(), equalTo(2L));
        assertThat(response.getContent().get(0).getEmail(), equalTo(UserData.ADMIN.getEmail()));
        assertThat(response.getContent().get(1).getEmail(), equalTo(UserData.MARTEN.getEmail()));
    }

    @Test
    public void create() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.create(AppUserCreate.builder()
                .username("create-new")
                .password("r0ckTheB@se")
                .email("new@rocketbase.io")
                .capabilityIds(Sets.newHashSet(CapabilityData.ROOT.getId()))
                .enabled(true)
                .build());

        // then
        assertThat(response, notNullValue());
        assertThat(response.getId(), notNullValue());
        assertThat(response.getCapabilities(), containsInAnyOrder(new AppCapabilityShort(CapabilityData.ROOT.getId(), CapabilityData.ROOT.getKeyPath())));
    }

    @Test
    public void patch() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.patch(getAppUser("user").getId(), AppUserUpdate.builder()
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .build());
        // then
        assertThat(response, notNullValue());
        assertThat(response.getFirstName(), equalTo("firstName"));
        assertThat(response.getLastName(), equalTo("lastName"));
        assertThat(response.getCapabilities().stream().map(AppCapabilityShort::getId).collect(Collectors.toSet()),
                equalTo(UserData.USER.getCapabilityIds()));
    }

    @Test
    public void resetPasswordValidPassword() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        AppUserEntity entity = appUserPersistenceService.findByUsername(user.getUsername()).get();
        String oldPassword = entity.getPassword() + "";
        Instant oldLastTokenInvalidation = entity.getLastTokenInvalidation();

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.resetPassword(user.getId(), new AppUserResetPassword("?:KTxg^*m4Q>j57:"));

        // then
        assertThat(response, notNullValue());
        entity = appUserPersistenceService.findByUsername(user.getUsername()).get();
        assertThat(entity.getPassword().equals(oldPassword), equalTo(false));
        assertThat(entity.getLastTokenInvalidation().isAfter(Nulls.notNull(oldLastTokenInvalidation, Instant.ofEpochMilli(0))), equalTo(true));
    }

    @Test
    public void resetPasswordInvalidPassword() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        try {
            appUserResource.resetPassword(getAppUser("user").getId(), new AppUserResetPassword("abc"));
        } catch (BadRequestException e) {
            // then
           assertThat(e.getErrorResponse().getMessage(), equalToIgnoringCase("Password not fitting requirements"));
        }
    }

    @Test
    public void delete() {
        // given
        JwtTokenProvider tokenProvider = getTokenProvider("admin");

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        appUserResource.delete(getAppUser("user").getId());
        // then
        assertThat(appUserPersistenceService.findByUsername("user").isPresent(), equalTo(false));

    }
}