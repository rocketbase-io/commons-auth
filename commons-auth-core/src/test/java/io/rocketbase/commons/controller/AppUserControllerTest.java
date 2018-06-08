package io.rocketbase.commons.controller;

import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.config.AuthProperties;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.resource.AppUserResource;
import io.rocketbase.commons.test.AppUserPersistenceTestService;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppUserControllerTest extends BaseIntegrationTest {


    @Resource
    private AppUserPersistenceTestService appUserPersistenceTestService;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Before
    public void beforeEachTest() {
        appUserPersistenceTestService.resetData();
    }

    @Test
    public void find() {
        // given
        AppUser user = getAppUser("admin");
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
    public void create() {
        // given
        AppUser user = getAppUser("admin");
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
        assertThat(response.getRoles(), containsInAnyOrder(new AuthProperties().getRoleUser()));
    }

    @Test
    public void patch() {
        // given
        AppUser user = getAppUser("admin");
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
        assertThat(response.getRoles(), containsInAnyOrder(new AuthProperties().getRoleUser()));
    }

    @Test
    public void delete() {
        // given
        AppUser user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        AppUserResource appUserResource = new AppUserResource(new JwtRestTemplate(tokenProvider));
        appUserResource.delete(getAppUser().getId());
        // then
        assertThat(appUserPersistenceTestService.findByUsername("user").isPresent(), equalTo(false));

    }
}