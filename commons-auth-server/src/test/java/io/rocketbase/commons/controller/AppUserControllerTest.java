package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTestPrefixed;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.*;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.resource.AppUserResource;
import io.rocketbase.commons.test.AppUserPersistenceTestService;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import io.rocketbase.commons.util.Nulls;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppUserControllerTest extends BaseIntegrationTestPrefixed {

    @Resource
    private AppUserPersistenceTestService appUserPersistenceTestService;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Test
    public void find() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

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
    public void findQuery() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        PageableResult<AppUserRead> response = appUserResource.find(QueryAppUser.builder().email("ISBALED").build(), PageRequest.of(0, 10));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(10));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findOne() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        Optional<AppUserRead> responseUser = appUserResource.findOne("user");
        Optional<AppUserRead> responseUnkownId = appUserResource.findOne("unkownId");

        // then
        assertThat(responseUser, notNullValue());
        assertThat(responseUser.isPresent(), equalTo(true));
        assertThat(responseUser.get().getUsername(), equalTo("user"));
        assertThat(responseUnkownId, notNullValue());
        assertThat(responseUnkownId.isPresent(), equalTo(false));
    }

    @Test
    public void create() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.create(AppUserCreate.builder()
                .username("create-new")
                .password("r0ckTheB@se")
                .email("new@rocketbase.io")
                .admin(false)
                .enabled(true)
                .build());

        // then
        assertThat(response, notNullValue());
        assertThat(response.getId(), notNullValue());
        assertThat(response.getRoles(), containsInAnyOrder(String.format("ROLE_%s", new AuthProperties().getRoleUser())));
    }

    @Test
    public void patch() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.patch(getAppUser().getId(), AppUserUpdate.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build());
        // then
        assertThat(response, notNullValue());
        assertThat(response.getFirstName(), equalTo("firstName"));
        assertThat(response.getLastName(), equalTo("lastName"));
        assertThat(response.getRoles(), containsInAnyOrder(String.format("ROLE_%s", new AuthProperties().getRoleUser())));
    }

    @Test
    public void resetPasswordValidPassword() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));
        AppUserTestEntity entity = appUserPersistenceTestService.findByUsername(user.getUsername()).get();
        String oldPassword = entity.getPassword() + "";
        Instant oldLastTokenInvalidation = entity.getLastTokenInvalidation();

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        AppUserRead response = appUserResource.resetPassword(user.getId(), new AppUserResetPassword("?:KTxg^*m4Q>j57:"));

        // then
        assertThat(response, notNullValue());
        entity = appUserPersistenceTestService.findByUsername(user.getUsername()).get();
        assertThat(entity.getPassword().equals(oldPassword), equalTo(false));
        assertThat(entity.getLastTokenInvalidation().isAfter(Nulls.notNull(oldLastTokenInvalidation, Instant.ofEpochMilli(0))), equalTo(true));
    }

    @Test
    public void resetPasswordInvalidPassword() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        try {
            appUserResource.resetPassword(getAppUser().getId(), new AppUserResetPassword("abc"));
        } catch (BadRequestException e) {
            // then
           assertThat(e.getErrorResponse().getMessage(), equalToIgnoringCase("Password not fitting requirements"));
        }
    }

    @Test
    public void delete() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        appUserResource.delete(getAppUser().getId());
        // then
        assertThat(appUserPersistenceTestService.findByUsername("user").isPresent(), equalTo(false));

    }
}