package io.rocketbase.commons.security;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenServiceTest {

    @Test
    public void generateTokenBundle() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builder()
                .id(UUID.randomUUID().toString())
                .username("test")
                .firstName("firstName")
                .lastName("lastName")
                .email("sample@rocketbase.io")
                .avatar("https://i.pravatar.cc/300")
                .roles(Arrays.asList("ROLE_USER"))
                .keyValueMap(ImmutableMap.of("special", "v1", "clientId", "1233"))
                .build();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("P0UoSCtNYlBlU2hWbVlxM3Q2dzl6JEMmRilKQE5jUmZUalduWnI0dTd4IUElRCpHLUthUGRTZ1ZrWHAyczV2OA==");
        JwtTokenService jwtTokenService = new JwtTokenService(jwtProperties, new EmptyCustomAuthoritiesProvider());

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        AppUserToken parsedAppUserToken = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(appUserToken, equalTo(parsedAppUserToken));
    }

    @Test
    public void checkKeyValueFiltered() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builder()
                .id(UUID.randomUUID().toString())
                .roles(Arrays.asList("ROLE_USER"))
                .keyValueMap(ImmutableMap.of("_secret", "v1", "clientId", "1233", "#hidden", "hidden"))
                .build();

        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("P0UoSCtNYlBlU2hWbVlxM3Q2dzl6JEMmRilKQE5jUmZUalduWnI0dTd4IUElRCpHLUthUGRTZ1ZrWHAyczV2OA==");
        JwtTokenService jwtTokenService = new JwtTokenService(jwtProperties, new EmptyCustomAuthoritiesProvider());

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        AppUserToken parsedAppUserToken = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(parsedAppUserToken.getId(), equalTo(appUserToken.getId()));
        assertThat(parsedAppUserToken.getRoles(), equalTo(appUserToken.getRoles()));
        assertThat(parsedAppUserToken.getKeyValues().size(), equalTo(1));
        assertThat(parsedAppUserToken.getKeyValue("#hidden"), nullValue());
        assertThat(parsedAppUserToken.getKeyValue("_secret"), nullValue());
        assertThat(parsedAppUserToken.getKeyValue("clientId"), equalTo("1233"));
    }
}