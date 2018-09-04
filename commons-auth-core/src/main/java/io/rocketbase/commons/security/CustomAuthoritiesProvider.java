package io.rocketbase.commons.security;

import io.rocketbase.commons.model.AppUser;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * provide extra authorities apart from the general user-roles stored in db
 */
public interface CustomAuthoritiesProvider {

    /**
     * will get injected in case of jwt-token creation<br>
     * that means they are also present in case of SecurityContext<br>
     * this should be considered for nearly static authorities that don't switch during a session
     *
     * @return a not nullable list of extra authorities / could also be an empty list
     */
    Collection<? extends GrantedAuthority> getExtraTokenAuthorities(String username);

    /**
     * will get injected in case of SecurityContext initialization<br>
     * you will find it in the security filter {@link io.rocketbase.commons.filter.JwtAuthenticationTokenFilter}<br>
     * this should be considered for nearly dynamic authorities<br>
     * keep in mind that also the extra token authorities will be present already
     *
     * @return a not nullable list of extra authorities / could also be an empty list
     */
    Collection<? extends GrantedAuthority> getExtraSecurityContextAuthorities(AppUser user, HttpServletRequest request);
}
