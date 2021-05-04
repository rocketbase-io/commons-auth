package io.rocketbase.commons.adapters;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.dto.authentication.LoginResponse;
import io.rocketbase.commons.resource.AppUserResource;
import io.rocketbase.commons.service.user.AppUserService;
import io.rocketbase.commons.test.data.UserData;
import io.rocketbase.commons.util.JwtTokenStore;
import io.rocketbase.commons.util.JwtTokenStoreHttp;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles(profiles = {"test", "token"})
public class AuthRestTemplateTest extends BaseIntegrationTest {

    @Resource
    private AppUserService appUserService;

    @Test
    public void testCorrectWorkflowWithValidInputs() throws InterruptedException {
        AuthClientLoginRequestFactory requestFactory = getAuthClientLoginRequestFactory();
        AuthRestTemplate restTemplate = new AuthRestTemplate(requestFactory);
        AppUserResource resource = new AppUserResource(getBaseUrl(), restTemplate);

        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle firstBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(firstBundle, notNullValue());
        assertThat(firstBundle.getToken(), notNullValue());
        assertThat(firstBundle.getRefreshToken(), notNullValue());

        // refresh access-token - profile token only generates 2sec valid access-tokens
        Thread.sleep(4500);
        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle secondBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(secondBundle, notNullValue());
        assertThat(secondBundle.getToken(), notNullValue());
        assertThat(firstBundle.getToken(), not(equalTo(secondBundle.getToken())));
        assertThat(firstBundle.getRefreshToken(), equalTo(secondBundle.getRefreshToken()));

        // wait until refreshToken is Invalid
        Thread.sleep(5500);
        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle thirdBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(thirdBundle, notNullValue());
        assertThat(thirdBundle.getToken(), notNullValue());
        assertThat(secondBundle.getToken(), not(equalTo(thirdBundle.getToken())));
        assertThat(secondBundle.getRefreshToken(), not(equalTo(thirdBundle.getRefreshToken())));
    }

    @Test
    public void testInvalidatedAccessToken() throws InterruptedException {
        AuthClientLoginRequestFactory requestFactory = getAuthClientLoginRequestFactory();
        AuthRestTemplate restTemplate = new AuthRestTemplate(requestFactory);
        AppUserResource resource = new AppUserResource(getBaseUrl(), restTemplate);

        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle firstBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(firstBundle, notNullValue());
        assertThat(firstBundle.getToken(), notNullValue());
        assertThat(firstBundle.getRefreshToken(), notNullValue());

        // refresh access-token - profile token only generates 2sec valid access-tokens
        Thread.sleep(4500);
        // active invalidate refreshToken by changing pw - that updates lastTokenInvalidation
        appUserService.updatePasswordUnchecked(UserData.ADMIN.getId(), "pw");

        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle secondBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(secondBundle, notNullValue());
        assertThat(secondBundle.getToken(), notNullValue());
        assertThat(firstBundle.getToken(), not(equalTo(secondBundle.getToken())));
        assertThat(firstBundle.getRefreshToken(), equalTo(secondBundle.getRefreshToken()));

        // wait until refreshToken is Invalid
        Thread.sleep(5500);
        resource.find(new QueryAppUser(), PageRequest.of(0, 10));
        JwtTokenBundle thirdBundle = new JwtTokenBundle(requestFactory.jwtTokenStore.getTokenBundle());
        assertThat(thirdBundle, notNullValue());
        assertThat(thirdBundle.getToken(), notNullValue());
        assertThat(secondBundle.getToken(), not(equalTo(thirdBundle.getToken())));
        assertThat(secondBundle.getRefreshToken(), not(equalTo(thirdBundle.getRefreshToken())));
    }

    private AuthClientLoginRequestFactory getAuthClientLoginRequestFactory() {
        AuthClientLoginRequestFactory requestFactory = new AuthClientLoginRequestFactory(getBaseUrl(), UserData.ADMIN.getUsername(), UserData.ADMIN.getPassword().replace("{noop}", "")) {
            @Override
            protected JwtTokenStore initStore(LoginResponse loginResponse) {
                return new JwtTokenStoreHttp(baseAuthApiUrl, loginResponse.getJwtTokenBundle()) {
                    @Override
                    public boolean checkTokenNeedsRefresh() {
                        return super.checkTokenNeedsRefresh(5);
                    }
                };
            }
        };
        return requestFactory;
    }


}