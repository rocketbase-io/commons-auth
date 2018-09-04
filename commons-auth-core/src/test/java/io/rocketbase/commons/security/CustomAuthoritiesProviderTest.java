package io.rocketbase.commons.security;

import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import io.rocketbase.commons.test.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CustomAuthoritiesProviderTest extends BaseIntegrationTest {

    public CustomAuthoritiesProvider myOwnImplementation() {
        return new CustomAuthoritiesProvider() {
            @Override
            public Collection<? extends GrantedAuthority> getExtraTokenAuthorities(String username) {
                return Arrays.asList(new SimpleGrantedAuthority("TOKEN_EXTRA"));
            }

            @Override
            public Collection<? extends GrantedAuthority> getExtraSecurityContextAuthorities(AppUser user, HttpServletRequest request) {
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
        config.setSecret("YSZESjg5aiZ0ZHszcSc1R0BEOiY5PFgp");
        JwtTokenService service = new JwtTokenService(config);
        service.customAuthoritiesProvider = myOwnImplementation();
        // when
        JwtTokenBundle jwtTokenBundle = service.generateTokenBundle(username, authorities);
        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(service.getAuthoritiesFromToken(jwtTokenBundle.getToken()),
                containsInAnyOrder(new SimpleGrantedAuthority("TOKEN_EXTRA"), new SimpleGrantedAuthority("ROLE_USER")));

    }
}