package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTestPrefixed;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.adapters.JwtTokenProvider;
import io.rocketbase.commons.adapters.SimpleJwtTokenProvider;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.resource.ImpersonateResource;
import io.rocketbase.commons.test.ModifiedJwtTokenService;
import io.rocketbase.commons.util.JwtTokenBody;
import io.rocketbase.commons.util.JwtTokenDecoder;
import org.junit.Test;

import javax.annotation.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ImpersonateControllerTest extends BaseIntegrationTestPrefixed {

    @Resource
    private ModifiedJwtTokenService modifiedJwtTokenService;

    @Test
    public void impersonate() {
        // given
        AppUserEntity admin = getAppUser("admin");
        JwtTokenProvider tokenProvider = new SimpleJwtTokenProvider(getBaseUrl(), modifiedJwtTokenService.generateTokenBundle(admin));
        AppUserEntity user = getAppUser("user");


        // when
        ImpersonateResource impersonateResource = new ImpersonateResource(new JwtRestTemplate(tokenProvider));
        JwtTokenBundle response = impersonateResource.impersonate(user.getId());

        // then
        assertThat(response, notNullValue());
        JwtTokenBody jwtTokenBody = JwtTokenDecoder.decodeTokenBody(response.getToken());
        assertThat(jwtTokenBody.getUsername(), equalTo(user.getUsername()));
        assertThat(jwtTokenBody.getScopes(), containsInAnyOrder("ROLE_" + user.getRoles().get(0)));
    }
}