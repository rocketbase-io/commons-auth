package io.rocketbase.commons.service;

import com.google.common.collect.Sets;
import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginRequest;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.resource.AuthenticationResource;
import io.rocketbase.commons.resource.LoginResource;
import io.rocketbase.commons.service.user.ActiveUserStore;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import io.rocketbase.commons.test.data.CapabilityData;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ActiveUserStoreIntegrationTest extends BaseIntegrationTest {

    @Resource
    private AppUserService appUserService;

    @Resource
    private AppUserConverter appUserConverter;

    @Resource
    private ActiveUserStore activeUserStore;

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Before
    public void beforeEachTest() {
        super.beforeEachTest();
        activeUserStore.clear();
    }

    @Test
    public void testEmpty() {
        // given

        // when

        // then
        assertThat(activeUserStore.getUserCount(), equalTo(0L));
    }

    @Test
    public void testMultipleLogins() {
        // given

        // when
        AuthenticationResource authenticationResource = new AuthenticationResource(new JwtRestTemplate(getTokenProvider(getAppUser("user"))));
        AppUserRead responseUser = authenticationResource.getAuthenticated();

        LoginResource loginResource = new LoginResource(getBaseUrl());
        LoginResponse loginResponse = loginResource.login(LoginRequest.builder()
                .username(getAppUser("admin").getEmail())
                .password("pw")
                .build());

        AppUserEntity newUser = appUserService.initializeUser(
                AppUserCreate.builder()
                        .username("test-123")
                        .password("pw")
                        .email("other@test.com")
                        .capabilityIds(Sets.newHashSet(CapabilityData.USER_READ.getId()))
                        .build());
        try {
            loginResource.login(LoginRequest.builder()
                    .username(newUser.getUsername())
                    .password("--")
                    .build());
            throw new RuntimeException("should not be possible to login");
        } catch (HttpClientErrorException.Unauthorized e) {
        }

        // then
        assertThat(activeUserStore.getUserCount(), equalTo(2L));
        assertThat(activeUserStore.getUserIds(), containsInAnyOrder(responseUser.getId(), loginResponse.getUser().getId()));
    }

    protected JwtTokenProvider getTokenProvider(AppUserEntity user) {
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(appUserConverter.toToken(user));

        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        tokenProvider.setRefreshToken(tokenBundle.getRefreshToken());
        String expiredToken = modifiedJwtTokenService.generateExpiredToken(user);
        tokenProvider.setToken(expiredToken);
        return tokenProvider;
    }

}
