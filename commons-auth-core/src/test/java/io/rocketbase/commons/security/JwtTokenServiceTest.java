package io.rocketbase.commons.security;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import org.assertj.core.util.Sets;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenServiceTest {

    @Test
    public void generateTokenBundle() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builderToken()
                .id(UUID.randomUUID().toString())
                .username("test")
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .avatar("https://i.pravatar.cc/300")
                        .build())
                .email("sample@rocketbase.io")
                .capabilities(Sets.newLinkedHashSet("blog/post"))
                .keyValues(ImmutableMap.of("special", "v1", "clientId", "1233"))
                .build();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("P0UoSCtNYlBlU2hWbVlxM3Q2dzl6JEMmRilKQE5jUmZUalduWnI0dTd4IUElRCpHLUthUGRTZ1ZrWHAyczV2OA==");
        JwtTokenService jwtTokenService = new JwtTokenService(jwtProperties, new EmptyCustomAuthoritiesProvider());

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        TokenParseResult meta = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(appUserToken, equalTo(meta.getUser()));
    }

    @Test
    public void checkKeyValueFiltered() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builderToken()
                .id(UUID.randomUUID().toString())
                .capabilities(Sets.newLinkedHashSet("ROLE_USER"))
                .keyValues(ImmutableMap.of("_secret", "v1", "clientId", "1233", "#hidden", "hidden"))
                .build();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("P0UoSCtNYlBlU2hWbVlxM3Q2dzl6JEMmRilKQE5jUmZUalduWnI0dTd4IUElRCpHLUthUGRTZ1ZrWHAyczV2OA==");
        JwtTokenService jwtTokenService = new JwtTokenService(jwtProperties, new EmptyCustomAuthoritiesProvider());

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        TokenParseResult meta = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(meta.getUser().getId(), equalTo(appUserToken.getId()));
        assertThat(meta.getUser().getCapabilities(), equalTo(appUserToken.getCapabilities()));
        assertThat(meta.getUser().getKeyValues().size(), equalTo(1));
        assertThat(meta.getUser().getKeyValue("#hidden"), nullValue());
        assertThat(meta.getUser().getKeyValue("_secret"), nullValue());
        assertThat(meta.getUser().getKeyValue("clientId"), equalTo("1233"));
    }
}