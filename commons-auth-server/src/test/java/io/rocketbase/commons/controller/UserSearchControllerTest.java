package io.rocketbase.commons.controller;

import io.rocketbase.commons.adapters.AuthRestTemplate;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.QueryAppUser;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.model.AppUserReference;
import io.rocketbase.commons.resource.UserSearchResource;
import io.rocketbase.commons.test.BaseIntegrationTest;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserSearchControllerTest extends BaseIntegrationTest {

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Test
    public void find() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        UserSearchResource userSearchResource = new UserSearchResource(new JwtRestTemplate(tokenProvider));
        PageableResult<AppUserReference> response = userSearchResource.search(new QueryAppUser(), PageRequest.of(0, 10));

        // then
        assertThat(response, notNullValue());
        assertThat(response.getTotalPages(), equalTo(1));
        assertThat(response.getPageSize(), equalTo(10));
        assertThat(response.getTotalElements(), greaterThan(2L));
    }

    @Test
    public void findByValidUsername() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        UserSearchResource userSearchResource = new UserSearchResource(new JwtRestTemplate(tokenProvider));
        Optional<AppUserReference> response = userSearchResource.findByUsernameOrId(user.getUsername());

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(true));
        assertThat(response.get().getId(), equalTo(user.getId()));
    }

    @Test
    public void findByInvalidUsername() {
        // given
        AppUserEntity user = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(user));

        // when
        UserSearchResource userSearchResource = new UserSearchResource(new JwtRestTemplate(tokenProvider));
        Optional<AppUserReference> response = userSearchResource.findByUsernameOrId("unkown-user");

        // then
        assertThat(response, notNullValue());
        assertThat(response.isPresent(), equalTo(false));
    }



    @Test
    public void testAuthRestTemplate() {
        // given
        AuthRestTemplate restTemplate = new AuthRestTemplate(getBaseUrl(), "user","pw");
        UserSearchResource resource = new UserSearchResource(getBaseUrl(), restTemplate);

        // when
        PageableResult<AppUserReference> response = resource.search(new QueryAppUser(), PageRequest.of(0, 1));

        // then
        assertThat(response, notNullValue());
    }



    @Test(expected = HttpClientErrorException.class)
    public void testInvalidAuthRestTemplate() {
        // given
        UserSearchResource resource = new UserSearchResource(getBaseUrl(), new RestTemplate());

        // when
        PageableResult<AppUserReference> response = resource.search(new QueryAppUser(), PageRequest.of(0, 1));

        // then
    }

}