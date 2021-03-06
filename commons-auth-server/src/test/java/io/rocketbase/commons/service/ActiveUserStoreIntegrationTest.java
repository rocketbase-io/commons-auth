package io.rocketbase.commons.service;

import io.rocketbase.commons.BaseIntegrationTestPrefixed;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
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
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ActiveUserStoreIntegrationTest extends BaseIntegrationTestPrefixed {

    @Resource
    private AppUserService appUserService;

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
        AuthenticationResource authenticationResource = new AuthenticationResource(new JwtRestTemplate(getTokenProvider(getAppUser())));
        AppUserRead responseUser = authenticationResource.getAuthenticated();

        LoginResource loginResource = new LoginResource(getBaseUrl());
        LoginResponse loginResponse = loginResource.login(LoginRequest.builder()
                .username(getAppAdminUser().getEmail())
                .password("pw")
                .build());

        AppUserEntity newUser = appUserService.initializeUser("test-123", "pw", "other@test.com", false);
        try {
            loginResource.login(LoginRequest.builder()
                    .username(newUser.getUsername())
                    .password("--")
                    .build());
            throw new RuntimeException("should not be possible to login");
        } catch (HttpClientErrorException e) {
        }

        // then
        assertThat(activeUserStore.getUserCount(), equalTo(2L));
        assertThat(activeUserStore.getUserIds(), containsInAnyOrder(responseUser.getId(), loginResponse.getUser().getId()));
    }

    protected JwtTokenProvider getTokenProvider(AppUserEntity user) {
        JwtTokenBundle tokenBundle = modifiedJwtTokenService.generateTokenBundle(user);

        SimpleJwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl());
        tokenProvider.setRefreshToken(tokenBundle.getRefreshToken());
        String expiredToken = modifiedJwtTokenService.generateExpiredToken(user);
        tokenProvider.setToken(expiredToken);
        return tokenProvider;
    }

}
