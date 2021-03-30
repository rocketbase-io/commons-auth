package io.rocketbase.commons.security;

import com.google.common.collect.Sets;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
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

    private AppUserToken genAppUser() {
        return SimpleAppUserToken.builderToken()
                .id(UUID.randomUUID().toString())
                .username("user")
                .email("email@rocketbase.io")
                .capabilities(Sets.newHashSet("USER"))
                .build();
    }

    @Test
    public void getUsernameFromToken() {
        // given
        AppUserToken appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        TokenParseResult parseToken = getInstance().parseToken(token);

        // then
        assertThat(parseToken, notNullValue());
        assertThat(parseToken.getUser().getUsername(), equalTo("user"));

    }

    @Test
    public void getAuthoritiesFromToken() {
        // given
        AppUserToken appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        TokenParseResult parseToken = getInstance().parseToken(token);

        // then
        assertThat(parseToken, notNullValue());
        assertThat(parseToken.getUser().getCapabilities().size(), equalTo(1));
        assertThat(parseToken.getUser().getCapabilities(), containsInAnyOrder("USER"));
    }

    @Test
    public void getIssuedAtDateFromToken() {
        // given
        Instant beforeCreate = Instant.now().minusSeconds(2);
        AppUserToken appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);
        Instant afterCreate = Instant.now().plusSeconds(2);

        // when
        TokenParseResult parseToken = getInstance().parseToken(token);

        // then
        assertThat(parseToken, notNullValue());
        assertThat(parseToken.getIssuedAt().isAfter(beforeCreate), equalTo(true));
        assertThat(parseToken.getIssuedAt().isBefore(afterCreate), equalTo(true));
    }

    @Test
    public void getExpirationDateFromToken() {
        // given
        Instant beforeCreate = Instant.now().minusSeconds(2);
        AppUserToken appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // when
        TokenParseResult parseToken = getInstance().parseToken(token);

        // then
        assertThat(parseToken, notNullValue());
        assertThat(parseToken.getExpiration().isAfter(beforeCreate
                .plusSeconds(60 * getInstance().jwtProperties.getAccessTokenExpiration())), equalTo(true));
        assertThat(parseToken.getExpiration().isBefore(beforeCreate
                .plusSeconds(60 * (getInstance().jwtProperties.getAccessTokenExpiration() + 1))), equalTo(true));

    }

    @Test
    public void validateToken() {
        // given
        AppUserToken appUser = genAppUser();

        // when
        String token = getInstance().generateAccessToken(Instant.now(), appUser);

        // then
        assertThat(getInstance().validateToken(token, genAppUser().getUsername(), null), equalTo(true));
    }

    @Test
    public void validateIssued() throws Exception {
        // given
        AppUserToken appUser = genAppUser();
        String token = getInstance().generateAccessToken(Instant.now(), appUser);
        Thread.sleep(100);

        // when
        Boolean validateToken = getInstance().validateToken(token, appUser.getUsername(), Instant.now());

        // then
        assertThat(validateToken, equalTo(false));
    }
}