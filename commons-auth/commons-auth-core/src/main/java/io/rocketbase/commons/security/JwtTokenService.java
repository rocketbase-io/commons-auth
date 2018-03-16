package io.rocketbase.commons.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import io.rocketbase.commons.config.AuthConfiguration;
import io.rocketbase.commons.converter.AppUserConverter;
import io.rocketbase.commons.dto.JwtTokenBundle;
import io.rocketbase.commons.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenService implements Serializable {

    @Resource
    private AuthConfiguration authConfiguration;

    @Resource
    private AppUserConverter appUserConverter;

    private AuthConfiguration.JwtConfiguration getJwt() {
        return authConfiguration.getJwt();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List roles = (List) claims.getOrDefault("scopes", Collections.emptyList());
        List<GrantedAuthority> result = new ArrayList<>();
        for (Object r : roles) {
            result.add(new SimpleGrantedAuthority(String.format("ROLE_%s", String.valueOf(r))));
        }
        return result;
    }

    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getJwt().getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtTokenBundle generateTokenBundle(AppUser user) {
        Date now = DefaultClock.INSTANCE.now();

        return new JwtTokenBundle(generateAccessToken(now, user),
                prepareBuilder(now, getJwt().getExpiration().getRefreshToken(), user.getUsername())
                        .claim("scopes", Arrays.asList("REFRESH_TOKEN"))
                        .compact());
    }


    public String generateAccessToken(AppUser user) {
        return generateAccessToken(DefaultClock.INSTANCE.now(), user);
    }

    private String generateAccessToken(Date now, AppUser user) {
        return prepareBuilder(now, getJwt().getExpiration().getAccessToken(), user.getUsername())
                .claim("scopes", user.getRoles())
                .claim("user", appUserConverter.fromEntity(user))
                .compact();
    }

    private JwtBuilder prepareBuilder(final Date createdDate, long expiration, String username) {
        return Jwts.builder()
                .setIssuedAt(createdDate)
                .setExpiration(new Date(createdDate.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS512, getJwt().getSecret())
                .setSubject(username);
    }

    public Boolean validateToken(String token, AppUser user) {
        if (!getUsernameFromToken(token).equals(user.getUsername())) {
            // username not fitting
            return false;
        }
        if (!getExpirationDateFromToken(token).before(DefaultClock.INSTANCE.now())) {
            // expired token
            return false;
        }
        if (user.getLastTokenInvalidation() == null) {
            return true;
        } else {
            // check if invalidation is newer then token creation
            Date created = getIssuedAtDateFromToken(token);
            Date lastTokenInvalidation = Date.from(user.getLastTokenInvalidation().atZone(ZoneId.systemDefault()).toInstant());
            return created.before(lastTokenInvalidation);
        }
    }
}
