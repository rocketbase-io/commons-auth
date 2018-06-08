package io.rocketbase.commons.security;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.model.AppUserTestEntity;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JwtTokenServiceTest {

    private ZoneId UTC = ZoneId.of("+0");

    private JwtTokenService getInstance() {
        JwtProperties config = new JwtProperties();
        config.setSecret("YSZESjg5aiZ0ZHszcSc1R0BEOiY5PFgp");
        JwtTokenService service = new JwtTokenService(config);
        return service;
    }

    private AppUser genAppUser() {
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
        String token = getInstance().generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), genAppUser());

        // when
        String username = getInstance().getUsernameFromToken(token);

        // then
        assertThat(username, notNullValue());
        assertThat(username, equalTo("user"));

    }

    @Test
    public void getAuthoritiesFromToken() {
        // given
        String token = getInstance().generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), genAppUser());

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
        LocalDateTime beforeCreate = LocalDateTime.now(UTC).minusSeconds(2);
        String token = getInstance().generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), genAppUser());
        LocalDateTime afterCreate = LocalDateTime.now(UTC).plusSeconds(2);

        // when
        LocalDateTime issued = getInstance().getIssuedAtDateFromToken(token);

        // then
        assertThat(issued, notNullValue());
        assertThat(issued.isAfter(beforeCreate), equalTo(true));
        assertThat(issued.isBefore(afterCreate), equalTo(true));
    }

    @Test
    public void getExpirationDateFromToken() {
        // given
        LocalDateTime beforeCreate = LocalDateTime.now(UTC).minusSeconds(2);
        String token = getInstance().generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), genAppUser());

        // when
        LocalDateTime expired = getInstance().getExpirationDateFromToken(token);

        // then
        assertThat(expired, notNullValue());
        assertThat(expired.isAfter(beforeCreate
                .plusMinutes(getInstance().jwtProperties.getAccessTokenExpiration())), equalTo(true));
        assertThat(expired.isBefore(beforeCreate
                .plusMinutes(getInstance().jwtProperties.getAccessTokenExpiration() + 1)), equalTo(true));

    }

    @Test
    public void validateToken() {
        // given

        // when
        String token = getInstance().generateAccessToken(LocalDateTime.now(ZoneOffset.UTC), genAppUser());

        // then
        assertThat(getInstance().validateToken(token, genAppUser()), equalTo(true));
    }
}