package io.rocketbase.commons.security;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUserToken;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.util.CapacityAuthoritiesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;

public class CustomAuthoritiesProviderTest extends BaseIntegrationTest {

    public CustomAuthoritiesProvider myOwnImplementation() {
        return new CustomAuthoritiesProvider() {
            @Override
            public Collection<? extends GrantedAuthority> getExtraTokenAuthorities(AppUserToken username) {
                return Arrays.asList(new SimpleGrantedAuthority("TOKEN_EXTRA"));
            }

            @Override
            public Collection<? extends GrantedAuthority> getExtraSecurityContextAuthorities(AppUserToken user, HttpServletRequest request) {
                return Arrays.asList(new SimpleGrantedAuthority("TEST"));
            }
        };
    }

    @Test
    public void getExtraSecurityContextAuthorities() {
        // given
        String username = "user";
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));

        JwtProperties config = new JwtProperties();
        config.setSecret("NHU3eCFBJUQqRy1LYU5kUmdVa1hwMnM1djh5L0I/RShIK01iUWVTaFZtWXEzdDZ3OXokQyZGKUpATmNSZlVqVw==");
        JwtTokenService service = new JwtTokenService(config, myOwnImplementation());
        // when
        JwtTokenBundle jwtTokenBundle = service.generateTokenBundle(SimpleAppUserToken.builderToken()
                .username(username)
                .capabilities(CapacityAuthoritiesConverter.convertToDtos(authorities))
                .build());
        // then
        assertThat(jwtTokenBundle, notNullValue());
        TokenParseResult parsedToken = service.parseToken(jwtTokenBundle.getToken());
        assertThat(parsedToken.getAuthoritiesFromToken(),
                containsInAnyOrder(new SimpleGrantedAuthority("TOKEN_EXTRA"), new SimpleGrantedAuthority("ROLE_USER")));

    }
}