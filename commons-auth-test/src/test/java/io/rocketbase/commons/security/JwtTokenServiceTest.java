package io.rocketbase.commons.security;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUserEntity;
import io.rocketbase.commons.test.model.AppUserTestEntity;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenServiceTest {

    private final ZoneId UTC = ZoneId.of("+0");

    private JwtTokenService getInstance() {
        JwtProperties config = new JwtProperties();
        config.setSecret("NHU3eCFBJUQqRy1LYU5kUmdVa1hwMnM1djh5L0I/RShIK01iUWVTaFZtWXEzdDZ3OXokQyZGKUpATmNSZlVqVw==");
        JwtTokenService service = new JwtTokenService(config, new EmptyCustomAuthoritiesProvider());
        return service;
    }

    private AppUserEntity genAppUser() {
        return AppUserTestEntity.builder()
                .id(UUID.randomUUID().toString())
                .username("user")
                .email("email@rocketbase.io")
                .enabled(true)
                .roles(Arrays.asList("USER"))
                .build();
    }

    @Test
    public void getUsernameFromToken() {
        // given
        AppUserEntity appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        String username = getInstance().getUsernameFromToken(token);

        // then
        assertThat(username, notNullValue());
        assertThat(username, equalTo("user"));

    }

    @Test
    public void getAuthoritiesFromToken() {
        // given
        AppUserEntity appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        Collection<? extends GrantedAuthority> authorities = getInstance().getAuthoritiesFromToken(token);

        // then
        assertThat(authorities, notNullValue());
        assertThat(authorities.size(), equalTo(1));
        assertThat(new ArrayList<>(authorities).get(0).getAuthority(), endsWith("USER"));
    }

    @Test
    public void getIssuedAtDateFromToken() {
        // given
        Instant beforeCreate = Instant.now().minusSeconds(2);
        AppUserEntity appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);
        Instant afterCreate = Instant.now().plusSeconds(2);

        // when
        Instant issued = getInstance().getIssuedAtDateFromToken(token);

        // then
        assertThat(issued, notNullValue());
        assertThat(issued.isAfter(beforeCreate), equalTo(true));
        assertThat(issued.isBefore(afterCreate), equalTo(true));
    }

    @Test
    public void getExpirationDateFromToken() {
        // given
        Instant beforeCreate = Instant.now().minusSeconds(2);
        AppUserEntity appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        Instant expired = getInstance().getExpirationDateFromToken(token);

        // then
        assertThat(expired, notNullValue());
        assertThat(expired.isAfter(beforeCreate
                .plusSeconds(60 * getInstance().jwtProperties.getAccessTokenExpiration())), equalTo(true));
        assertThat(expired.isBefore(beforeCreate
                .plusSeconds(60 * (getInstance().jwtProperties.getAccessTokenExpiration() + 1))), equalTo(true));

    }

    @Test
    public void validateToken() {
        // given
        AppUserEntity appUser = genAppUser();

        // when
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // then
        assertThat(getInstance().validateToken(token, genAppUser()), equalTo(true));
    }

    @Test
    public void validateIssued() throws Exception {
        // given
        AppUserEntity appUser = genAppUser();
        appUser.updateLastTokenInvalidation();
        Thread.sleep(1001);

        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        Boolean validateToken = getInstance().validateToken(token, appUser);

        // then
        assertThat(validateToken, equalTo(true));
    }
}